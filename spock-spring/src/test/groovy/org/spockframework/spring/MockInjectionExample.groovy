package org.spockframework.spring;

import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import spock.lang.Specification;

@ContextConfiguration(locations = "classpath:MockExamples-context.xml")
public class MockInjectionExample extends Specification {

  @Autowired
  IService1 service1

  @Autowired
  IService2 service2

  def "Injected services are mocks"() {
    expect:
    new MockUtil().isMock(service1)
    new MockUtil().isMock(service2)
  }

  def "Mocks can be configured"() {
    when:
    assert service1.generateString() == "I can be configured"
    assert service2.generateQuickBrownFox() == "The quick brown fox..."

    then:
    1 * service1.generateString() >> "I can be configured"
    1 * service2.generateQuickBrownFox() >> "The quick brown fox..."
  }

  def "Unconfigured mocks return default"() {
    expect:
    service1.generateString() == null
    service2.generateQuickBrownFox() == null

  }

}
