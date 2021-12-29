package org.spockframework.guice

import com.google.inject.Inject

class Service3 implements IService3 {
  private final IService1 service1
  private final Service2 service2

  @Inject
  Service3(IService1 service1, Service2 service2) {
    this.service1 = service1
    this.service2 = service2
  }

  @Override
  String combinedString() {
    return service1.generateString() + '\n' + service2.generateQuickBrownFox()
  }
}
