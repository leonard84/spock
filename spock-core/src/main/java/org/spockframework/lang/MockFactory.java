package org.spockframework.lang;

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

}
