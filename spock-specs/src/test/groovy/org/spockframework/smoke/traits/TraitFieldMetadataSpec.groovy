/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spockframework.smoke.traits

import spock.lang.Subject

import java.lang.reflect.Modifier

import org.spockframework.EmbeddedSpecification
import org.spockframework.runtime.SpecInfoBuilder
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.FieldMetadata
import spock.lang.Issue

@Issue("https://github.com/spockframework/spock/issues/1551")
class TraitFieldMetadataSpec extends EmbeddedSpecification {

  def "trait-declared field gets a FieldInfo with un-mangled name"() {
    when:
    def specClass = compiler.compile('''
      trait T {
        int x = 10
      }
      class ATraitSpec extends spock.lang.Specification implements T {
        def feature() {
          expect: x == 10
        }
      }
    ''').first()

    def specInfo = new SpecInfoBuilder(specClass).build()
    def fields = specInfo.fields

    then:
    fields*.name.contains('x')
  }

  def "annotation on trait field gets the @FieldMetadata annotation on the composed JVM field"() {
    when:
    def specClass = compiler.compile('''
      trait T {
        @spock.lang.Subject
        Object component = new Object()
      }
      class ASubjectTraitSpec extends spock.lang.Specification implements T {
        def feature() {
          expect: component != null
        }
      }
    ''').first()

    // The JVM-level field is mangled, e.g. T__component.
    def mangled = specClass.declaredFields.find { it.name.endsWith('__component') }

    then:
    mangled != null
    mangled.isAnnotationPresent(FieldMetadata)
    mangled.getAnnotation(FieldMetadata).name() == 'component'

    and: "the @Subject annotation propagates through Groovy's trait composer"
    mangled.isAnnotationPresent(Subject)
  }

  def "@AutoCleanup on a trait field is honoured at runtime"() {
    given:
    ResourceHolder.counter.set(0)

    when:
    runner.runWithImports('''
      trait Resourceful {
        @spock.lang.AutoCleanup
        Closeable resource = { -> org.spockframework.smoke.traits.ResourceHolder.counter.incrementAndGet() } as Closeable
      }
      class AutoCleanupTraitSpec extends spock.lang.Specification implements Resourceful {
        def feature() {
          expect: resource != null
        }
      }
    ''')

    then:
    ResourceHolder.counter.get() == 1L
  }

  def "FieldInfo.readValue and writeValue work for trait-declared fields"() {
    when:
    def specClass = compiler.compile('''
      trait T {
        int counter = 1
      }
      class ARwTraitSpec extends spock.lang.Specification implements T {
        def feature() {
          expect: counter == 1
        }
      }
    ''').first()

    def specInfo = new SpecInfoBuilder(specClass).build()
    FieldInfo counterField = specInfo.fields.find { it.name == 'counter' }

    def instance = specClass.getDeclaredConstructor().newInstance()
    def initial = counterField.readValue(instance)
    counterField.writeValue(instance, 42)
    def updated = counterField.readValue(instance)

    then:
    counterField != null
    initial == 1
    updated == 42
  }

  def "two traits each declaring a field both contribute FieldInfos with distinct ordinals"() {
    when:
    def specClass = compiler.compile('''
      trait A { int alpha = 1 }
      trait B { int beta = 2 }
      class TwoTraitsSpec extends spock.lang.Specification implements A, B {
        def feature() {
          expect: alpha + beta == 3
        }
      }
    ''').first()

    def specInfo = new SpecInfoBuilder(specClass).build()
    def names = specInfo.fields*.name
    def ordinals = specInfo.fields*.ordinal

    then:
    names.containsAll(['alpha', 'beta'])
    ordinals.toSet().size() == specInfo.fields.size() // all distinct
  }

  def "transitively inherited trait fields are discovered"() {
    when:
    def specClass = compiler.compile('''
      trait Inner { int innerField = 1 }
      trait Outer implements Inner { int outerField = 2 }
      class TransitiveTraitSpec extends spock.lang.Specification implements Outer {
        def feature() {
          expect: innerField + outerField == 3
        }
      }
    ''').first()

    def specInfo = new SpecInfoBuilder(specClass).build()

    then:
    specInfo.fields*.name.containsAll(['innerField', 'outerField'])
  }

  def "@Shared on a trait field produces a compile error"() {
    when:
    compiler.compile('''
      trait T {
        @spock.lang.Shared
        int counter = 0
      }
      class SharedTraitFieldSpec extends spock.lang.Specification implements T {
        def feature() {
          expect: counter == 0
        }
      }
    ''')

    then:
    def e = thrown(Exception)
    e.message.contains('@Shared is not supported on trait-declared fields')
  }

  def "trait field ordinals are appended after spec-declared field ordinals"() {
    when:
    def specClass = compiler.compile('''
      trait T { int traitField = 1 }
      class OrderedTraitSpec extends spock.lang.Specification implements T {
        int specField = 2
        def feature() {
          expect: specField + traitField == 3
        }
      }
    ''').first()

    def specInfo = new SpecInfoBuilder(specClass).build()
    def specFieldOrdinal = specInfo.fields.find { it.name == 'specField' }.ordinal
    def traitFieldOrdinal = specInfo.fields.find { it.name == 'traitField' }.ordinal

    then:
    traitFieldOrdinal > specFieldOrdinal
  }
}
