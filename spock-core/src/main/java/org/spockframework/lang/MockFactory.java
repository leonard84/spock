package org.spockframework.lang;

import groovy.lang.Closure;
import org.spockframework.util.Beta;

import java.util.Map;


public interface MockFactory {
  /**
   * Creates a mock with the specified type. If enclosed in a variable assignment, the variable name will be
   * used as the mock's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Mock(Person) // type is Person.class, name is "person"
   * </pre>
   *
   * @param type the interface or class type of the mock
   * @param <T> the interface or class type of the mock
   *
   * @return a mock with the specified type
   */
  <T> T Mock(Class<T> type);

  /**
   * Creates a mock with the specified options and type. If enclosed in an variable assignment, the variable name
   * will be used as the mock's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Mock(Person, name: "myPerson") // type is Person.class, name is "myPerson"
   * </pre>
   *
   * @param options optional options for creating the mock
   * @param type the interface or class type of the mock
   * @param <T> the interface or class type of the mock
   *
   * @return a mock with the specified options and type
   */
  @Beta
  <T> T Mock(Map<String, Object> options, Class<T> type);

  /**
   * Creates a mock with the specified type and interactions. If enclosed in a variable assignment, the variable name will be
   * used as the mock's name.
   *
   * Example:
   *
   * <pre>
   *   // name is "person", type is Person.class, returns hard-code value {@code name}, expects one call to {@code sing()}
   *   def person = Mock(Person) {
   *     name << "Fred"
   *     1 * sing()
   *   }
   * </pre>
   *
   * @param type the interface or class type of the mock
   * @param interactions a description of the mock's interactions
   * @param <T> the interface or class type of the mock
   *
   * @return a mock with the specified type and interactions
   */
  @Beta
  <T> T Mock(Class<T> type, Closure interactions);

  /**
   * Creates a mock with the specified options, type, and interactions. If enclosed in a variable assignment, the
   * variable name will be used as the mock's name.
   *
   * Example:
   *
   * <pre>
   *   // type is Person.class, name is "myPerson", returns hard-coded value {@code name}, expects one call to {@code sing()}
   *   def person = Mock(Person, name: "myPerson") {
   *     name << "Fred"
   *     1 * sing()
   *   }
   * </pre>
   *
   * @param options options for creating the mock (see {@link org.spockframework.mock.IMockConfiguration} for available options})
   * @param type the interface or class type of the mock
   * @param interactions a description of the mock's interactions
   * @param <T> the interface or class type of the mock
   *
   * @return a mock with the specified options, type, and interactions
   */
  @Beta
  <T> T Mock(Map<String, Object> options, Class<T> type, Closure interactions);

  /**
   * Creates a stub with the specified type. If enclosed in a variable assignment, the variable name will be
   * used as the stub's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Stub(Person) // type is Person.class, name is "person"
   * </pre>
   *
   * @param type the interface or class type of the stub
   * @param <T> the interface or class type of the stub
   *
   * @return a stub with the specified type
   */
  @Beta
  <T> T Stub(Class<T> type);

  /**
   * Creates a stub with the specified options and type. If enclosed in an variable assignment, the variable name
   * will be used as the stub's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Stub(Person, name: "myPerson") // type is Person.class, name is "myPerson"
   * </pre>
   *
   * @param options optional options for creating the stub
   * @param type the interface or class type of the stub
   * @param <T> the interface or class type of the stub
   *
   * @return a stub with the specified options and type
   */
  @Beta
  <T> T Stub(Map<String, Object> options, Class<T> type);

  /**
   * Creates a stub with the specified type and interactions. If enclosed in a variable assignment, the variable name will be
   * used as the stub's name.
   *
   * Example:
   *
   * <pre>
   *   // name is "person", type is Person.class, returns hard-coded values for property {@code name} and method {@code sing()}
   *   def person = Stub(Person) {
   *     name << "Fred"
   *     sing() << "Tra-la-la"
   *   }
   * </pre>
   *
   * @param type the interface or class type of the stub
   * @param interactions a description of the stub's interactions
   * @param <T> the interface or class type of the stub
   *
   * @return a stub with the specified type and interactions
   */
  @Beta
  <T> T Stub(Class<T> type, Closure interactions);

  /**
   * Creates a stub with the specified options, type, and interactions. If enclosed in a variable assignment, the
   * variable name will be used as the stub's name.
   *
   * Example:
   *
   * <pre>
   *   // type is Person.class, name is "myPerson", returns hard-coded values for property {@code name} and method {@code sing()}
   *   def person = Stub(Person, name: "myPerson") {
   *     name << "Fred"
   *     sing() << "Tra-la-la"
   *   }
   * </pre>
   *
   * @param options options for creating the stub (see {@link org.spockframework.mock.IMockConfiguration} for available options})
   * @param type the interface or class type of the stub
   * @param interactions a description of the stub's interactions
   * @param <T> the interface or class type of the stub
   *
   * @return a stub with the specified options, type, and interactions
   */
  @Beta
  <T> T Stub(Map<String, Object> options, Class<T> type, Closure interactions);

  /**
   * Creates a spy with the specified type. If enclosed in a variable assignment, the variable name will be
   * used as the spy's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Spy(Person) // type is Person.class, name is "person"
   * </pre>
   *
   * @param type the class type of the spy
   * @param <T> the class type of the spy
   *
   * @return a spy with the specified type
   */
  @Beta
  <T> T Spy(Class<T> type);

  /**
   * Creates a spy with the specified options and type. If enclosed in an variable assignment, the variable name
   * will be used as the spy's name.
   *
   * Example:
   *
   * <pre>
   *   def person = Spy(Person, name: "myPerson") // type is Person.class, name is "myPerson"
   * </pre>
   *
   * @param options optional options for creating the spy
   * @param type the class type of the spy
   * @param <T> the class type of the spy
   *
   * @return a spy with the specified options and type
   */
  @Beta
  <T> T Spy(Map<String, Object> options, Class<T> type);

  /**
   * Creates a spy with the specified type and interactions. If enclosed in a variable assignment, the variable name will be
   * used as the spy's name.
   *
   * Example:
   *
   * <pre>
   *   // name is "person", type is Person.class, returns hard-code value {@code name}, calls real method otherwise
   *   def person = Spy(Person) {
   *     name << "Fred"
   *     1 * sing()
   *   }
   * </pre>
   *
   * @param type the class type of the spy
   * @param interactions a description of the spy's interactions
   * @param <T> the class type of the spy
   *
   * @return a spy with the specified type and interactions
   */
  @Beta
  <T> T Spy(Class<T> type, Closure interactions);

  /**
   * Creates a spy with the specified options, type, and interactions. If enclosed in a variable assignment, the
   * variable name will be used as the spy's name.
   *
   * Example:
   *
   * <pre>
   *   // type is Person.class, name is "myPerson", returns hard-coded value {@code name}, calls real method otherwise
   *   def person = Spy(Person, name: "myPerson") {
   *     name << "Fred"
   *   }
   * </pre>
   *
   * @param options options for creating the spy (see {@link org.spockframework.mock.IMockConfiguration} for available options})
   * @param type the class type of the spy
   * @param interactions a description of the spy's interactions
   * @param <T> the class type of the spy
   *
   * @return a spy with the specified options, type, and interactions
   */
  @Beta
  <T> T Spy(Map<String, Object> options, Class<T> type, Closure interactions);
}
