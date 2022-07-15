package org.spockframework.smoke.ast

import org.spockframework.EmbeddedSpecification
import spock.lang.DataProvider

class StandaloneDataProviderSpec extends EmbeddedSpecification {

  def "transforms standalone data provider"() {
    given:
    compiler.addClassImport(DataProvider)
    when:
    def result = compiler.transpileWithImports('''
    class SomeClass {
      @DataProvider
      def someProvider() {
        a | b
        1 | 2
        3 | 4
      }
     }
  ''')

    then:
    result.source == '''\
'''
  }
}
