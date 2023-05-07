package org.spockframework.smoke.ast


import org.spockframework.EmbeddedSpecification
import org.spockframework.specs.extension.Snapshot
import org.spockframework.specs.extension.Snapshotter

class OldAstSpec extends EmbeddedSpecification {
  @Snapshot(extension = 'groovy')
  Snapshotter snapshotter

  def "old expression"() {
    given:
    snapshotter.featureBody()

    when:
    def result = compiler.transpileFeatureBody('''
    given:
    def list = [1]

    when:
    list.add(2)

    then:
    list.size() == old(list.size()) + 1

    when:
    list.add(3)

    then:
    list.size() == old(list.size()) + 1
    list.size() == 3
    ''')

    then:
    snapshotter.assertThat(result.source).matchesSnapshot()
  }
}
