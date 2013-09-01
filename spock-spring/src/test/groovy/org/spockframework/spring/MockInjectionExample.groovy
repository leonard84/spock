package org.spockframework.spring;

import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import spock.lang.Specification;

@ContextConfiguration(locations = "classpath:MockExamples-context.xml")
public class MockInjectionExample extends Specification {

  @Autowired
  IService1 service1

  def "Injected service is mock"() {
    expect:
    new MockUtil().isMock(service1)
  }

  def "Mock can be configured"() {
    when:
    assert service1.generateString() == "I can be configured"

    then:
    1 * service1.generateString() >> "I can be configured"
  }
}
