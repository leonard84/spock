package org.spockframework.guice

import com.google.inject.Inject
import spock.guice.GuiceSpyInterceptor
import spock.guice.UseModules
import spock.lang.Specification

@UseModules(Module3)
class Spy2Spec extends Specification {
  @Inject
  IService3 service3

  @GuiceSpyInterceptor
  IService1 service1

  @GuiceSpyInterceptor
  Service2 service2

  def "works without stubbing"() {
    expect:
    service3.combinedString() == 'foo\nThe quick brown fox jumps over the lazy dog.'
  }

  def "can intercept interface usage"() {
    when:
    def result = service3.combinedString()

    then:
    result == 'bar\nThe quick brown fox jumps over the lazy dog.'
    1 * service1.generateString() >> 'bar'
  }

  def "can intercept class usage"() {
    when:
    def result = service3.combinedString()

    then:
    result == 'foo\nbar'
    1 * service2.generateQuickBrownFox() >> 'bar'
  }
}
