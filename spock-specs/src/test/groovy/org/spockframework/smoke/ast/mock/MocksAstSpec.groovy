package org.spockframework.smoke.ast.mock

import org.spockframework.EmbeddedSpecification
import org.spockframework.specs.extension.Snapshot
import org.spockframework.specs.extension.Snapshotter

class MocksAstSpec  extends EmbeddedSpecification {

  @Snapshot(extension = 'groovy')
  Snapshotter snapshotter

  def "simple interaction"() {
    given:
    snapshotter.featureBody()

    when:
    def result = compiler.transpileFeatureBody("""
    given:
    List list = Mock()

    when:
    list.add(1)

    then:
    1 * list.add(1)
""")
    then:
    snapshotter.assertThat(result.source).matchesSnapshot()
  }

  def "ordered interaction"() {
    given:
    snapshotter.featureBody()

    when:
    def result = compiler.transpileFeatureBody("""
    given:
    List list = Mock()

    when:
    list.add(1)
    list.add(2)
    list.add(3)

    then:
    1 * list.add(1)
    then:
    1 * list.add(2)
    then:
    1 * list.add(3)
""")
    then:
    snapshotter.assertThat(result.source).matchesSnapshot()
  }

  def "interaction conditions"() {
    given:
    snapshotter.featureBody()

    when:
    def result = compiler.transpileFeatureBody("""
    given:
    List list = Mock()

    when:
    10.times { list.add(1) }

    then:
    1 * list.add(1)
    (1..2) * list.add({ i < 10 })
    (1.._) * list.add(_ as Integer)
    _ * list.add(_)
    _ * _._
    _ * _
""")
    then:
    snapshotter.assertThat(result.source).matchesSnapshot()
  }

  def "interaction responses"() {
    given:
    snapshotter.featureBody()

    when:
    def result = compiler.transpileFeatureBody("""
    given:
    List list = Mock()

    when:
    list.get(1)
    list.get(2)
    list.get(3)
    list.get(3)

    then:
    1 * list.get(1) >> 1
    1 * list.get(2) >> { it[0] * 2 }
    2 * list.get(3) >> 1 >> { it[0] * 3 }
""")
    then:
    snapshotter.assertThat(result.source).matchesSnapshot()
  }
}
