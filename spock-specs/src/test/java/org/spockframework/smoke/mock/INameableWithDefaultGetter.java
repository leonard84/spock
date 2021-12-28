package org.spockframework.smoke.mock;

public interface INameableWithDefaultGetter {
  default String getName() {
    return "Bob";
  }

  void setName(String name);
}
