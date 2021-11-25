package org.spockframework.guice

import static java.util.Collections.singleton

class MockModule extends MockFactoryModule {
  MockModule() {
    super(singleton(IService1), singleton(IService2))
  }
}
