package org.spockframework.guice

import com.google.inject.AbstractModule

class Module3 extends AbstractModule {
  @Override
  protected void configure() {
    bind(IService1).to(Service1)
    bind(Service2)
    bind(IService3).to(Service3)
  }
}
