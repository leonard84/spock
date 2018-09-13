/*
 * Copyright 2009 the original author or authors.
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

import org.spockframework.mock.TooFewInvocationsError
import spock.lang.*

class ArgumentMatching extends Specification {
  def "match identical argument"() {
    List list = Mock()

    when: list.add(arg)
    then: 1 * list.add(arg)

    where: arg << [1, "foo", new Object()]
  }

  def "match equal argument"() {
    List list = Mock()

    when: list.add(arg1)
    then: 1 * list.add(arg2)

    where:
      arg1 << [1, [1,2,3] as Set, null]
      arg2 << [1.0, [3,2,1] as Set, null]
  }

  def "match empty argument list"() {
    List list = Mock()

    when: list.size()
    then: 1 * list.size()
  }

  def "match named argument (empty args)"() {
    NamedArgs na = Mock()

    when: na.m([:])
    then: 1 * na.m([:])
  }

  def "match named argument (named args only, map expression)"() {
    NamedArgs na = Mock()

    when: na.m(foo: 'bar')
    then: 1 * na.m(foo: 'bar')
  }

  def "match named argument (named args only, explicit map)"() {
    NamedArgs na = Mock()

    when: na.m([foo: 'bar'])
    then: 1 * na.m([foo: 'bar'])
  }

  def "match named argument (named args only, map expression and positional arguments)"() {
    NamedArgs na = Mock()

    when: na.m('test', foo: 'bar')
    then: 1 * na.m('test', foo: 'bar')
  }

  def "match named argument (named args only, explicit map and positional arguments)"() {
    NamedArgs na = Mock()

    when: na.m([foo: 'bar'], 'test')
    then: 1 * na.m([foo: 'bar'], 'test')
  }

  def "match named argument with type matcher (named args only, map expression)"() {
    NamedArgs na = Mock()

    when: na.m(foo: 'bar')
    then: 1 * na.m(foo: _ as String)
  }

  @FailsWith(TooFewInvocationsError)
  def "match named argument with type matcher (named args only, explicit map)"() {
    NamedArgs na = Mock()

    when: na.m([foo: 'bar'])
    then: 1 * na.m([foo:  _ as String])
  }

  @PendingFeature(reason = "InteractionRewriter must be changed to make this work")
  def "match named argument with type matcher (named args only, map expression and positional arguments)"() {
    NamedArgs na = Mock()

    when: na.m('test', foo: 'bar')
    then: 1 * na.m('test', foo:  _ as String)
  }

  @FailsWith(TooFewInvocationsError)
  def "match named argument with type matcher (named args only, explicit map and positional arguments)"() {
    NamedArgs na = Mock()

    when: na.m([foo: 'bar'], 'test')
    then: 1 * na.m([foo:  _ as String], 'test')
  }

  @FailsWith(TooFewInvocationsError)
  def "expect fewer arguments than are provided"() {
    Overloads overloads = Mock()

    when:
    overloads.m("one")

    then:
    1 * overloads.m()
  }

  @FailsWith(TooFewInvocationsError)
  def "expect more arguments than are provided"() {
    Overloads overloads = Mock()

    when:
    overloads.m()

    then:
    1 * overloads.m("one")
  }

  @FailsWith(TooFewInvocationsError)
  def "expect fewer arguments than are provided (w/ varargs)"() {
    Varargs2 varargs = Mock()

    when:
    varargs.m("one")

    then:
    1 * varargs.m()
  }

  @FailsWith(TooFewInvocationsError)
  def "expect more arguments than are provided (w/ varargs)"() {
    Varargs2 varargs = Mock()

    when:
    varargs.m()

    then:
    1 * varargs.m("one")
  }

  interface Overloads {
    void m()
    void m(String one)
  }

  interface Varargs2 {
    void m(String... strings)
  }

  interface NamedArgs {
    void m(Map args)
    void m(Map args, String a)
  }
}

