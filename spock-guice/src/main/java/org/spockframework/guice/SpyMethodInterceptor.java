package org.spockframework.guice;

import groovy.lang.GroovyObject;
import org.spockframework.gentyref.GenericTypeReflector;
import org.spockframework.mock.*;
import org.spockframework.mock.runtime.*;
import org.spockframework.runtime.GroovyRuntimeUtil;
import org.spockframework.util.ExceptionUtil;

import org.spockframework.util.ReflectionUtil;
import spock.lang.Specification;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import groovy.lang.MetaClass;
import org.aopalliance.intercept.*;

public class SpyMethodInterceptor extends BaseMockInterceptor implements MethodInterceptor {
  private final IMockConfiguration mockConfiguration;
  private final MetaClass mockMetaClass;
  private Specification specification;
  private MockController fallbackMockController;

  public SpyMethodInterceptor(IMockConfiguration mockConfiguration, Specification specification, MetaClass mockMetaClass) {
    this.mockConfiguration = mockConfiguration;
    this.specification = specification;
    this.mockMetaClass = mockMetaClass;
  }

  @Override
  public Object invoke(MethodInvocation invocation) {
    Object target = invocation.getThis();
    Method method = invocation.getMethod();
    Object[] arguments = invocation.getArguments();
    return intercept(target, method, arguments, __ -> {
      try {
        return invocation.proceed();
      } catch (Throwable e) {
        throw (RuntimeException)ExceptionUtil.sneakyThrow(e);
      }
    });
  }

  @Override
  public Object intercept(Object target, Method method, Object[] arguments, IResponseGenerator realMethodInvoker) {
    IMockObject mockObject = new SpyMockObject(mockConfiguration.getName(), mockConfiguration.getExactType(),
      target, mockConfiguration.isVerified(), mockConfiguration.getDefaultResponse(), specification, this);

    if (method.getDeclaringClass() == ISpockMockObject.class) {
      return mockObject;
    }

    // here no instances of org.codehaus.groovy.runtime.wrappers.Wrapper subclasses
    // should arrive in the arguments array. If there are some found, it should first
    // be investigated whether they should have made it until here. If it is correct
    // that they arrived here, maybe GroovyRuntimeUtil.asUnwrappedArgumentArray needs
    // to be used to unwrap the arguments. Wrapper subclasses are used to transport
    // type cast information to select proper overloaded methods.
    Object[] args = GroovyRuntimeUtil.asArgumentArray(arguments);

    if (target instanceof GroovyObject) {
      if (isMethod(method, "getMetaClass")) {
        return mockMetaClass;
      }
      if (isMethod(method, "setProperty", String.class, Object.class)) {
        Throwable throwable = new Throwable();
        StackTraceElement mockCaller = throwable.getStackTrace()[3];
        if ("org.codehaus.groovy.runtime.ScriptBytecodeAdapter".equals(mockCaller.getClassName())
          || "org.codehaus.groovy.runtime.InvokerHelper".equals(mockCaller.getClassName())) {
          // HACK: for some reason, runtime dispatches direct property access on mock classes via ScriptBytecodeAdapter
          // delegate to the corresponding setter method
          // for abstract groovy classes and interfaces it uses InvokerHelper
          String methodName = GroovyRuntimeUtil.propertyToMethodName("set", (String)args[0]);
          return GroovyRuntimeUtil.invokeMethod(target, methodName, GroovyRuntimeUtil.asArgumentArray(args[1]));
        }
      }
      if (isMethod(method, "getProperty", String.class)) {
        String methodName = handleGetProperty((GroovyObject)target, args);
        if (methodName != null) {
          return GroovyRuntimeUtil.invokeMethod(target, methodName);
        }
      }
    }

    IMockMethod mockMethod = new StaticMockMethod(method, target.getClass());
    IMockInvocation invocation = new MockInvocation(mockObject, mockMethod, Arrays.asList(args), realMethodInvoker);
    IMockController mockController = specification == null ? getFallbackMockController() :
      specification.getSpecificationContext().getMockController();

    return mockController.handle(invocation);
  }

  @Override
  public void attach(Specification specification) {
    this.specification = specification;

  }

  public MockController getFallbackMockController() {
    if (fallbackMockController == null) {
      fallbackMockController = new MockController();
    }
    return fallbackMockController;
  }

  @Override
  public void detach() {
    this.specification = null;
  }

  private static class SpyMockObject extends MockObject {

    private final Class<?> type;

    public SpyMockObject(String name, Type type, Object instance, boolean verified, IDefaultResponse defaultResponse, Specification specification, SpecificationAttachable mockInterceptor) {
      super(name, type, instance, verified, true, defaultResponse, specification, mockInterceptor);
      this.type = GenericTypeReflector.erase(type);
    }

    @Override
    protected boolean matchGlobal(Object target) {
      return type.isInstance(target);
    }
  }
}
