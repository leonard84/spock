package org.spockframework.smoke.mock

import org.spockframework.mock.CannotCreateMockException
import spock.lang.Specification

class AdditionalInterfaces extends Specification {

  def "java stubs"() {
    given:
    def stub = Stub(List, additionalInterfaces: [Closeable])

    expect:
    stub instanceof List
    stub instanceof Closeable

    when:
    stub.close()

    then:
    noExceptionThrown()
  }

  def "java stubs of GroovyObjects"() {
    given:
    def stub = Stub(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    stub instanceof List
    stub instanceof Closeable

    when:
    stub.close()

    then:
    noExceptionThrown()
  }

  def "java mocks"() {
    given:
    def mock = Mock(List, additionalInterfaces: [Closeable])

    expect:
    mock instanceof List
    mock instanceof Closeable

    when:
    mock.close()

    then:
    1 * mock.close() >> {}
  }

  def "java mocks of GroovyObjects"() {
    given:
    def mock = Mock(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    mock instanceof List
    mock instanceof Closeable

    when:
    mock.close()

    then:
    1 * mock.close() >> {}
  }

  def "java spies"() {
    given:
    def spy = Spy(ArrayList, additionalInterfaces: [Closeable])

    expect:
    spy instanceof List
    spy instanceof Closeable

    when:
    spy.close()

    then:
    1 * spy.close() >> {}
  }

  def "java spies of GroovyObjects"() {
    given:
    def spy = Spy(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    spy instanceof List
    spy instanceof Closeable

    when:
    spy.close()

    then:
    1 * spy.close() >> {}
  }

  def "groovy stubs"() {
    given:
    def stub = GroovyStub(List, additionalInterfaces: [Closeable])

    expect:
    stub instanceof List
    stub instanceof Closeable

    when:
    stub.close()

    then:
    noExceptionThrown()
  }

  def "groovy stubs of GroovyObjects"() {
    given:
    def stub = GroovyStub(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    stub instanceof List
    stub instanceof Closeable

    when:
    stub.close()

    then:
    noExceptionThrown()
  }

  def "groovy mocks"() {
    given:
    def mock = GroovyMock(List, additionalInterfaces: [Closeable])

    expect:
    mock instanceof List
    mock instanceof Closeable

    when:
    mock.close()

    then:
    1 * mock.close() >> {}
  }

  def "groovy mocks of GroovyObjects"() {
    given:
    def mock = GroovyMock(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    mock instanceof List
    mock instanceof Closeable

    when:
    mock.close()

    then:
    1 * mock.close() >> {}
  }

  def "groovy spies"() {
    given:
    def spy = GroovySpy(ArrayList, additionalInterfaces: [Closeable])

    expect:
    spy instanceof List
    spy instanceof Closeable

    when:
    spy.close()

    then:
    1 * spy.close() >> {}
  }

  def "groovy spies of GroovyObjects"() {
    given:
    def spy = GroovySpy(AGroovyClass, additionalInterfaces: [Closeable])

    expect:
    spy instanceof List
    spy instanceof Closeable

    when:
    spy.close()

    then:
    1 * spy.close() >> {}
  }

  def "groovy mocks for final class cannot have additionalInterfaces"() {
    when:
    GroovyMock(String, additionalInterfaces: [Closeable])

    then:
    thrown(CannotCreateMockException)
  }


  def "groovy global mocks cannot have additionalInterfaces"() {
    when:
    GroovyMock(ArrayList, additionalInterfaces: [Closeable], global: true)

    then:
    thrown(CannotCreateMockException)
  }
}

class AGroovyClass {}
