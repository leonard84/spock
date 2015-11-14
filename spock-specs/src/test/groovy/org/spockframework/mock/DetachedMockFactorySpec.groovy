package org.spockframework.mock

import spock.lang.Specification
import spock.lang.Subject

class DetachedMockFactorySpec extends Specification {

  @Subject
  DetachedMockFactory factory = new DetachedMockFactory()

  def "Mock(class)"() {
    given:
    IMockMe mock = factory.Mock(IMockMe)
    attach(mock)

    when:
    mock.foo(2)

    then:
    1 * mock.foo(2)
    getMockName(mock) == 'IMockMe'

    cleanup:
    detach(mock)
  }

  def "Mock(options, class)"() {
    given:
    IMockMe mock = factory.Mock(IMockMe, name: 'customName')
    attach(mock)

    when:
    mock.foo(2)

    then:
    1 * mock.foo(2)
    getMockName(mock) == 'customName'

    cleanup:
    detach(mock)
  }

  private String getMockName(IMockMe mock) {
    new MockUtil().asMock(mock).name
  }

  void attach(Object mock) {
    new MockUtil().attachMock(mock, this)
  }

  void detach(Object mock) {
    new MockUtil().detachMock(mock)
  }
}

interface IMockMe {
  def foo(int i)
}
