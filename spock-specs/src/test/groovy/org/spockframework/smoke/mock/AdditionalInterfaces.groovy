package org.spockframework.smoke.mock

import org.spockframework.mock.CannotCreateMockException
import spock.lang.Specification

class AdditionalInterfaces extends Specification {

  def "java stubs"() {
    given:
    def stub = Stub(List, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    stub instanceof List
    stub instanceof Closeable
    stub instanceof INameableWithDefaultGetter

    when:
    stub.close()
    stub.name = "foo"
    def result = stub.name

    then:
    result == ""
    noExceptionThrown()
  }

  def "java stubs of GroovyObjects"() {
    given:
    def stub = Stub(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    stub instanceof AGroovyClass
    stub instanceof Closeable
    stub instanceof INameableWithDefaultGetter

    when:
    stub.close()
    stub.name = "foo"
    def result = stub.name

    then:
    result == ""
    noExceptionThrown()
  }

  def "java mocks"() {
    given:
    def mock = Mock(List, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    mock instanceof List
    mock instanceof Closeable
    mock instanceof INameableWithDefaultGetter

    when:
    mock.close()
    mock.name = "foo"
    def result = mock.name

    then:
    result == "bar"
    1 * mock.close() >> {}
    1 * mock.name >> "bar"
    1 * mock.setName("foo")
  }

  def "java mocks of GroovyObjects"() {
    given:
    def mock = Mock(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    mock instanceof AGroovyClass
    mock instanceof Closeable
    mock instanceof INameableWithDefaultGetter

    when:
    mock.close()
    mock.name = "foo"
    def result = mock.name

    then:
    result == "bar"
    1 * mock.close() >> {}
    1 * mock.name >> "bar"
    1 * mock.setName("foo")
  }

  def "java spies"() {
    given:
    def spy = Spy(ArrayList, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    spy instanceof List
    spy instanceof Closeable
    spy instanceof INameableWithDefaultGetter

    when:
    spy.close()
    spy.name = "foo"
    def result = spy.name

    then:
    result == "Bob"
    1 * spy.close() >> {}
    1 * spy.name
    1 * spy.setName("foo") >> {}
  }

  def "java spies of GroovyObjects"() {
    given:
    def spy = Spy(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    spy instanceof AGroovyClass
    spy instanceof Closeable
    spy instanceof INameableWithDefaultGetter

    when:
    spy.close()
    spy.name = "foo"
    def result = spy.name

    then:
    result == "Bob"
    1 * spy.close() >> {}
    1 * spy.name
    1 * spy.setName("foo") >> {}
  }

  def "groovy stubs"() {
    given:
    def stub = GroovyStub(List, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    stub instanceof List
    stub instanceof Closeable
    stub instanceof INameableWithDefaultGetter

    when:
    stub.close()
    stub.name = "foo"
    def result = stub.name

    then:
    result == ""
    noExceptionThrown()
  }

  def "groovy stubs of GroovyObjects"() {
    given:
    def stub = GroovyStub(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    stub instanceof AGroovyClass
    stub instanceof Closeable
    stub instanceof INameableWithDefaultGetter

    when:
    stub.close()
    stub.name = "foo"
    def result = stub.name

    then:
    result == ""
    noExceptionThrown()
  }

  def "groovy mocks"() {
    given:
    def mock = GroovyMock(List, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    mock instanceof List
    mock instanceof Closeable
    mock instanceof INameableWithDefaultGetter

    when:
    mock.close()
    mock.name = "foo"
    def result = mock.name

    then:
    result == "bar"
    1 * mock.close() >> {}
    1 * mock.name >> "bar"
    1 * mock.setName("foo")
  }

  def "groovy mocks of GroovyObjects"() {
    given:
    def mock = GroovyMock(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    mock instanceof AGroovyClass
    mock instanceof Closeable
    mock instanceof INameableWithDefaultGetter

    when:
    mock.close()
    mock.name = "foo"
    def result = mock.name

    then:
    result == "bar"
    1 * mock.close() >> {}
    1 * mock.name >> "bar"
    1 * mock.setName("foo")
  }

  def "groovy spies"() {
    given:
    def spy = GroovySpy(ArrayList, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    spy instanceof List
    spy instanceof Closeable
    spy instanceof INameableWithDefaultGetter

    when:
    spy.close()
    spy.name = "foo"
    def result = spy.name

    then:
    result == "Bob"
    1 * spy.close() >> {}
    1 * spy.name
    1 * spy.setName("foo") >> {}
  }

  def "groovy spies of GroovyObjects"() {
    given:
    def spy = GroovySpy(AGroovyClass, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    expect:
    spy instanceof AGroovyClass
    spy instanceof Closeable
    spy instanceof INameableWithDefaultGetter

    when:
    spy.close()
    spy.name = "foo"
    def result = spy.name

    then:
    result == "Bob"
    1 * spy.close() >> {}
    1 * spy.name
    1 * spy.setName("foo") >> {}
  }

  def "groovy mocks for final class cannot have additionalInterfaces"() {
    when:
    GroovyMock(String, additionalInterfaces: [Closeable, INameableWithDefaultGetter])

    then:
    thrown(CannotCreateMockException)
  }


  def "groovy global mocks cannot have additionalInterfaces"() {
    when:
    GroovyMock(ArrayList, additionalInterfaces: [Closeable, INameableWithDefaultGetter], global: true)

    then:
    thrown(CannotCreateMockException)
  }
}

class AGroovyClass {
  String getBla() {
    return "Bla"
  }
}
