/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.smoke.mock

import org.spockframework.EmbeddedSpecification
import org.spockframework.mock.TooFewInvocationsError

import org.hamcrest.CoreMatchers

class TooFewInvocations extends EmbeddedSpecification {
  def "shows unmatched invocations, ordered by similarity"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)
def list2 = Mock(List)
def map = Mock(Map)

when:
list.remove(2)
list2.add(2)
list.add(1)
map.size()
list.remove(2)

then:
1 * list.add(2)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * list.add(2)   (0 invocations)

Unmatched invocations (ordered by similarity):

1 * list.add(1)
2 * list.remove(2)
1 * list2.add(2)
1 * map.size()
    """.trim()
  }

  def "makes it clear if no unmatched invocations exist"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)

when:
def x = 1

then:
1 * list.add(_)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * list.add(_)   (0 invocations)

Unmatched invocations (ordered by similarity):

None
    """.trim()
  }

  def "shows all unsatisfied interactions"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)

when:
list.add(2)
list.remove(2)

then:
1 * list.add(1)
1 * list.remove(1)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * list.add(1)   (0 invocations)

Unmatched invocations (ordered by similarity):

1 * list.add(2)
1 * list.remove(2)

Too few invocations for:

1 * list.remove(1)   (0 invocations)

Unmatched invocations (ordered by similarity):

1 * list.remove(2)
1 * list.add(2)
    """.trim()
  }

  def "does not show unverified invocations"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)
def list2 = Stub(List)

when:
list.add(1)
list2.add(2)

then:
1 * list.add(2)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.contains("1 * list.add(1)")
    !e.message.contains("1 * list2.add(2)")
  }

  def "does not show invocations in outer scopes"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)

list.add(1)

when:
list.add(2)

then:
2 * list.add(_)
    """)

    then:
    TooFewInvocationsError e = thrown()
    !e.message.contains("list.add(1)")
  }

  def "can describe string argument mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Fred", "Flintstone", 30, "Quarry")

then:
1 * fred.wife("Wilma", "Flintstone", 30, "Bedrock")
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe code argument mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife("Wilma", "Flintstone", { it < 30}, "Bedrock")
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe hamcrest matcher argument mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.addClassImport(CoreMatchers)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife("Wilma", "Flintstone", CoreMatchers.equalTo(20), "Bedrock")
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe type argument mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife("Wilma", "Flintstone", _ as String, _ as String)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe type not enough args"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife("Wilma", "Flintstone",30, "Bedrock", _ as String)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe type too many args"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife("Wilma", "Flintstone",30)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe regex method name mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred./kids?/("Wilma", "Flintstone", 30, "Bedrock")
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe regex property name mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
def name = fred.age

then:
1 * fred./name?/
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }


  def "can describe property name mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
def name = fred.age

then:
1 * fred.name
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe namedArgument on normal method mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.wife("Wilma", "Flintstone", 30, "Bedrock")

then:
1 * fred.wife(firstName: "Wilma", surname: "Flintstone", age: _ as String, address: _ as String)
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }

  def "can describe namedArgument on map method mismatch"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)

when:
fred.familiy(firstName: "Wilma", surname: "Flintstone", age: 30, address: "Quarry")

then:
1 * fred.familiy(firstName: "Wilma", surname: "Flintstone", age: _ as String, address: "Bedrock")
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() == """
Too few invocations for:

1 * fred.wife("Fred", "Flintstone", 30, "Quarry")   (0 invocations)

Unmatched invocations (ordered by similarity):

    """.trim()
  }


  def "can compute similarity between an interaction and a completely different invocation (without blowing up)"() {
    when:
    runner.addClassImport(Person)
    runner.runFeatureBody("""
def fred = Mock(Person)
def barney = Mock(Person)

when:
fred.age = 30

then:
1 * barney.setName { it.size() > 10 } // actual call passes an int, which has no size() method
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.contains("barney.setName")
  }


  def "renders with dump() if no better toString exists"() {
    when:
    runner.runFeatureBody("""
def list = Mock(List)
def person = new org.spockframework.smoke.mock.TooFewInvocations.Person(age: 18, name: 'Foo')

when:
list.add(person)

then:
1 * list.add('bar')
    """)

    then:
    TooFewInvocationsError e = thrown()
    e.message.trim() =~ /(?m)^\Q1 * list.add(<org.spockframework.smoke.mock.TooFewInvocations\E.Person@\w+ name=Foo age=18>\)$/
  }

  static class Person {
    String name
    int age

    String wife(String firstName, String surname, int age, String address){
      return ''
    }

    String familiy(Map args) {
      return ''
    }
  }
}
