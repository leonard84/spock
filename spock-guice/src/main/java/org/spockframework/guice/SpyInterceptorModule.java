package org.spockframework.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import org.spockframework.mock.ISpockMockObject;
import org.spockframework.util.Pair;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpyInterceptorModule extends AbstractModule {
  private final Map<String, Class> spyInterceptors;

  public SpyInterceptorModule(Map<String, Class> spyInterceptors) {
    this.spyInterceptors = spyInterceptors;
  }

  @Override
  protected void configure() {
    if (spyInterceptors.isEmpty()) return;

    SpockGuiceSpyProxyCreator creator = new SpockGuiceSpyProxyCreator();
    Multibinder<ISpockMockObject> multiBinder = Multibinder.newSetBinder(binder(), ISpockMockObject.class);
    for (Map.Entry<String, Class> entry : spyInterceptors.entrySet()) {
      Pair<SpyMethodInterceptor, Object> interceptorAndSpy = creator.createSpy(entry.getValue(), entry.getKey());

      bindInterceptor(
        Matchers.subclassesOf(entry.getValue()),
        NonSyntheticMethodMatcher.INSTANCE.and(new ImplementedMethodMatcher(entry.getValue())),
        interceptorAndSpy.first());

      multiBinder.addBinding().toInstance((ISpockMockObject) interceptorAndSpy.second());
    }
  }

  private static class NonSyntheticMethodMatcher extends AbstractMatcher<Method> {
    private static final NonSyntheticMethodMatcher INSTANCE = new NonSyntheticMethodMatcher();

    @Override
    public boolean matches(Method method) {
      return !method.isSynthetic();
    }
  }

  private static class ImplementedMethodMatcher extends AbstractMatcher<Method> {
    private final Set<Method> methods;

    private ImplementedMethodMatcher(Class clazz) {
      methods = getMethods(clazz).collect(Collectors.toSet());
    }

    private static Stream<Method> getMethods(Class clazz) {
      if (clazz == Object.class || clazz == null) return Stream.empty();
      return Stream.concat(
        Stream.concat(
            Stream.of(clazz.getInterfaces()),
            Stream.of(clazz.getSuperclass())
          )
          .flatMap(ImplementedMethodMatcher::getMethods),
        Stream.of(clazz.getDeclaredMethods()));
    }


    @Override
    public boolean matches(Method method) {
      return methods.stream().anyMatch(m ->
        Objects.equals(m.getName(), method.getName())
          && m.getReturnType() == method.getReturnType()
          && m.getParameterCount() == method.getParameterCount()
          && Arrays.equals(m.getParameterTypes(), method.getParameterTypes())
      );
    }
  }
}
