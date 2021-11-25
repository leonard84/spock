package org.spockframework.guice;

import org.spockframework.mock.*;
import org.spockframework.mock.runtime.*;
import org.spockframework.runtime.GroovyRuntimeUtil;
import org.spockframework.util.Pair;

import java.util.Collections;

import groovy.lang.MetaClass;

public class SpockGuiceSpyProxyCreator {

  @SuppressWarnings("unchecked")
  <T> Pair<SpyMethodInterceptor, T> createSpy(Class<T> type, String name) {
    MockConfiguration mockConfiguration = new MockConfiguration(name, type, MockNature.SPY, MockImplementation.JAVA, Collections.singletonMap("global", true));
    MetaClass mockMetaClass = GroovyRuntimeUtil.getMetaClass(type);
    SpyMethodInterceptor interceptor = new SpyMethodInterceptor(mockConfiguration, null, mockMetaClass);
    Object spyInstance = ProxyBasedMockFactory.INSTANCE.create(type, Collections.emptyList(), null,
      interceptor, SpockGuiceSpyProxyCreator.class.getClassLoader(), true);

    return Pair.of(interceptor, (T) spyInstance);
  }
}
