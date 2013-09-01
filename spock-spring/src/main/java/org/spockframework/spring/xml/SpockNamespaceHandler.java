package org.spockframework.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Adds support for the spock namespace.
 *
 * Spring integration of spock mocks is heavily inspired by
 * Springokito {@link https://bitbucket.org/kubek2k/springockito}.
 *
 * @author Leonard Bruenings
 */
public class SpockNamespaceHandler extends NamespaceHandlerSupport {

  public void init() {
    registerBeanDefinitionParser("mock", new MockBeanDefinitionParser());
  }
}
