package org.spockframework.guice;

import org.spockframework.mock.*;
import org.spockframework.util.Pair;

import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;

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

      bindInterceptor(Matchers.subclassesOf(entry.getValue()), Matchers.any(), interceptorAndSpy.first());

      multiBinder.addBinding().toInstance((ISpockMockObject)interceptorAndSpy.second());
    }
  }
}
