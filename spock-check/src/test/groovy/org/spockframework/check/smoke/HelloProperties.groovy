package org.spockframework.check.smoke

import net.jqwik.api.Arbitraries
import org.spockframework.check.Property
import spock.lang.Specification

class HelloProperties extends Specification {

  @Property
  def "can run with Arbitraries"() {
    expect:
    a < b

    where:
    a = Arbitraries.integers().between(1, 100)
    b = Arbitraries.integers().between(100, 200)
  }
}
