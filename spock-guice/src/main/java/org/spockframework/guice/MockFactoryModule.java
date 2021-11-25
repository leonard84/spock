package org.spockframework.guice;

import spock.mock.DetachedMockFactory;

import java.util.Set;

import com.google.inject.AbstractModule;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MockFactoryModule extends AbstractModule {
  protected DetachedMockFactory mockFactory = new DetachedMockFactory();

  protected final Set<Class> mocks;
  protected final Set<Class> stubs;

  protected MockFactoryModule(Set<Class> mocks, Set<Class> stubs) {
    this.mocks = mocks;
    this.stubs = stubs;
  }

  @Override
  protected void configure() {
    mocks.forEach(mock -> bind(mock).toInstance(mockFactory.Mock(mock)));
    stubs.forEach(stubs -> bind(stubs).toInstance(mockFactory.Stub(stubs)));
  }
}
