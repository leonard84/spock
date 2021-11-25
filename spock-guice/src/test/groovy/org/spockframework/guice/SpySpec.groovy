package org.spockframework.guice

import spock.guice.GuiceSpyInterceptor
import spock.guice.UseModules
import spock.lang.Specification

import com.google.inject.Inject

@UseModules(Module1)
class SpySpec extends Specification {

  @Inject
  IService1 service

  @GuiceSpyInterceptor
  IService1 serviceSpy

  def "service works as normal"() {
    expect:
    service.generateString() == "foo"
  }

  def "serviceSpy works acts as spy"() {
    when:
    def result = service.generateString()

    then:
    result == "foo"
    1 * serviceSpy.generateString()
  }

  def "serviceSpy works can intercept"() {
    when:
    def result = service.generateString()

    then:
    result == "bar"
    1 * serviceSpy.generateString() >> "bar"
  }
}
