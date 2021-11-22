/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.util

import spock.lang.*

import java.lang.annotation.Annotation

import io.leangen.geantyref.TypeToken

class ReflectionUtilSpec extends Specification {
  def "get package name"() {
    expect:
    ReflectionUtil.getPackageName(ReflectionUtilSpec) == "org.spockframework.util"
  }

  def "load existing class"() {
    expect:
    ReflectionUtil.loadClassIfAvailable("java.util.List") == List
  }

  def "load unexisting class"() {
    expect:
    ReflectionUtil.loadClassIfAvailable("not.AvailableClass") == null
  }

  def "check if class exists"() {
    expect:
    ReflectionUtil.isClassAvailable("java.util.List")
    !ReflectionUtil.isClassAvailable("not.AvailableClass")
  }

  def "check if method exists"() {
    expect:
    ReflectionUtil.isMethodAvailable("java.util.List", "size")
    !ReflectionUtil.isMethodAvailable("java.util.List", "mice")
  }

  def "check if annotation of certain type is present"() {
    expect:
    ReflectionUtil.isAnnotationPresent(Stepwise, "org.spockframework.runtime.extension.ExtensionAnnotation")
    !ReflectionUtil.isAnnotationPresent(Stepwise, "foo.bar.Baz")
  }

  static final class FinalClass {
    void foo() {}
  }

  static class FinalMethod {
    final void foo() {}
  }

  def "check if method is final"() {
    expect:
    ReflectionUtil.isFinalMethod(FinalClass.getDeclaredMethod("foo"))
    ReflectionUtil.isFinalMethod(FinalMethod.getDeclaredMethod("foo"))
  }

  def "find method by name"() {
    expect:
    ReflectionUtil.getMethodByName(Derived, "baseMethod") == Base.getDeclaredMethod("baseMethod", String, int)
  }

  def "find declared method by name"() {
    expect:
    ReflectionUtil.getDeclaredMethodByName(Derived, "derivedMethod") == Derived.getDeclaredMethod("derivedMethod", String)
  }

  def "find method by name and parameter types"() {
    expect:
    ReflectionUtil.getMethodBySignature(Derived, "overloadedMethod", int) == Derived.getDeclaredMethod("overloadedMethod", int)
    ReflectionUtil.getMethodBySignature(Derived, "overloadedMethod", List) == null
  }

  def "find declared method by name and parameter types"() {
    expect:
    ReflectionUtil.getDeclaredMethodBySignature(Derived, "derivedMethod", String) == Derived.getDeclaredMethod("derivedMethod", String)
    ReflectionUtil.getDeclaredMethodBySignature(Derived, "derivedMethod", List) == null
  }

  def "find class file for a class"() {
    expect:
    def file = ReflectionUtil.getClassFile(ReflectionUtilSpec)
    file != null
    file.exists()
    file.name == "ReflectionUtilSpec.class"
  }

  def "get default value for a type"() {
    expect:
    ReflectionUtil.getDefaultValue(type) == defaultValue

    where:
    type    | defaultValue
    boolean | false
    byte    | 0
    short   | 0
    char    | 0
    int     | 0
    long    | 0
    float   | 0
    double  | 0
    Object  | null
    void    | null
    Void    | null
  }

  def "check if a value has one of several types"() {
    expect:
    ReflectionUtil.hasAnyOfTypes([1], Map, ArrayList, Set)
    ReflectionUtil.hasAnyOfTypes([1], Map, Collection, Set)
    !ReflectionUtil.hasAnyOfTypes([1], Map, Set)
    !ReflectionUtil.hasAnyOfTypes([1])
  }

  def "get types of several objects"() {
    expect:
    ReflectionUtil.getTypes([1], [a: 1]) == [ArrayList, LinkedHashMap] as Class[]
    ReflectionUtil.getTypes() == [] as Class[]
  }

  def "invoke good method"() {
    def method = InvokeMe.getDeclaredMethod("good", int)

    expect:
    ReflectionUtil.invokeMethod(new InvokeMe(), method, 42) == 42
  }

  def "invoke method that throws unchecked exception"() {
    def method = InvokeMe.getDeclaredMethod("unchecked")

    when:
    ReflectionUtil.invokeMethod(new InvokeMe(), method)

    then:
    thrown(IllegalArgumentException)
  }

  def "invoke method that throws checked exception"() {
    def method = InvokeMe.getDeclaredMethod("checked")

    when:
    ReflectionUtil.invokeMethod(new InvokeMe(), method)

    then:
    thrown(IOException)
  }

  def "erase types"() {
    def method = Generics.methods.find { it.name == "foo" }
    def types = method.getGenericParameterTypes() as List

    expect:
    ReflectionUtil.eraseTypes(types) == [Object, List, List]
  }


  def "detects overridden toString"(Class clazz) {
    expect:
    ReflectionUtil.isToStringOverridden(clazz)

    where:
    clazz << [String, ArrayList, AbstractCollection]
  }

  def "detects not overridden toString"(Class clazz) {
    expect:
    !ReflectionUtil.isToStringOverridden(clazz)

    where:
    clazz << [Object, Base, InvokeMe, Generics]
  }

  def "resolve parameter types"() {
    def method = Generics.methods.find { it.name == "foo" }

    expect:
    ReflectionUtil.getResolvedParameterTypes(method, Generics) == [Object, List, List]
  }

  def "resolve bounded parameter types"() {
    def method = BoundedGenerics.methods.find { it.name == "foo" }

    expect:
    ReflectionUtil.getResolvedParameterTypes(method, BoundedGenerics) == [Number, List, List]
  }

  def "resolve return types"() {
    def method = Generics.methods.find { it.name == "bar" }

    expect:
    ReflectionUtil.getResolvedReturnType(method, Generics) == Object
  }

  def "resolve bounded return types"() {
    def method = BoundedGenerics.methods.find { it.name == "bar" }

    expect:
    ReflectionUtil.getResolvedReturnType(method, BoundedGenerics) == Number
  }

  def "resolve field types"() {
    def field = Generics.declaredFields.find { it.name == "value" }

    expect:
    ReflectionUtil.getResolvedFieldType(field, Generics) == Object
  }

  def "resolve bounded field types"() {
    def field = BoundedGenerics.declaredFields.find { it.name == "value" }

    expect:
    ReflectionUtil.getResolvedFieldType(field, BoundedGenerics) == Number
  }

  def "resolve inherited parameter types"() {
    def method = Child.methods.find { it.name == "foo" }
    def integerList = new TypeToken<List<Integer>>() {}.type
    def stringList = new TypeToken<List<String>>() {}.type

    expect:
    ReflectionUtil.getResolvedParameterTypes(method, Child) == [Integer, integerList, stringList]
  }

  def "resolve inherited return types"() {
    def method = Child.methods.find { it.name == "bar" }

    expect:
    ReflectionUtil.getResolvedReturnType(method, Child) == Integer
  }

  def "resolve inherited field types"() {
    def field = BoundedGenerics.declaredFields.find { it.name == "value" }

    expect:
    ReflectionUtil.getResolvedFieldType(field, Child) == Integer
  }


  def "resolve bounded local return type"() {
    def method = Child.methods.find { it.name == "getAnnotation" }

    expect:
    ReflectionUtil.getResolvedReturnType(method, Child) == Annotation
  }


  static class Base {
    def baseMethod(String arg1, int arg2) {}
  }

  static class Derived extends Base {
    private derivedMethod(String arg) {}

    void overloadedMethod(String arg) {}

    void overloadedMethod(int arg) {}
  }

  static class InvokeMe {
    def good(int arg) { arg }
    def unchecked() { throw new IllegalArgumentException("oops") }
    def checked() { throw new IOException("ouch") }
  }

  static class Generics<T> {
    T value
    void foo(T one, List<T> two, List<String> three) {}
    T bar(){ null }
  }

  static class BoundedGenerics<T extends Number> {
    T value
    void foo(T one, List<T> two, List<String> three) {}
    T bar(){ null }

    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
      return null
    }
  }


  static class Child extends BoundedGenerics<Integer> {
  }
}
