package org.spockframework.smoke.mock;

public interface InterfaceWithDefaultMethod {
  String normalMethod();

  default String defaultMethod() {
    return normalMethod();
  }
}
