package org.spockframework.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SpockNamespaceHandler extends NamespaceHandlerSupport {

  public void init() {
    registerBeanDefinitionParser("mock", new MockBeanDefinitionParser());
  }
}
