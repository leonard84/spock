package org.spockframework.guice;

import org.spockframework.mock.IMockConfiguration;
import org.spockframework.mock.runtime.JavaMockInterceptor;
import org.spockframework.util.ExceptionUtil;

import spock.lang.Specification;

import java.lang.reflect.Method;

import groovy.lang.MetaClass;
import org.aopalliance.intercept.*;

public class SpyMethodInterceptor extends JavaMockInterceptor implements MethodInterceptor {
  public SpyMethodInterceptor(IMockConfiguration mockConfiguration, Specification specification, MetaClass mockMetaClass) {
    super(mockConfiguration, specification, mockMetaClass);
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
}
