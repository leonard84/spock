package org.spockframework.spring.xml;

import java.util.Collections;

import org.spockframework.mock.MockImplementation;
import org.spockframework.mock.MockNature;
import org.spockframework.mock.MockUtil;
import org.springframework.beans.factory.FactoryBean;

/**
 * Takes care of instantiating detached spock Mocks.
 *
 * Spring integration of spock mocks is heavily inspired by
 * Springokito {@link https://bitbucket.org/kubek2k/springockito}.
 *
 * @author Leonard Bruenings
 */
public class SpockMockFactoryBean<T> implements FactoryBean<T> {

  private final Class<T> targetClass;
  private String name;

  private T instance;

  public SpockMockFactoryBean (Class<T> targetClass) {
    this.targetClass = targetClass;
  }

  @SuppressWarnings("unchecked")
  public T getObject() throws Exception {
    if (instance == null) {
      ClassLoader classLoader = targetClass.getClassLoader();
      if (classLoader == null) {
        classLoader = ClassLoader.getSystemClassLoader();
      }
      instance = (T) new MockUtil().createDetachedMock(name, targetClass, MockNature.MOCK, MockImplementation.JAVA, Collections.EMPTY_MAP, classLoader);
    }
    return instance;
  }

  public Class<?> getObjectType() {
    return targetClass;
  }

  public boolean isSingleton() {
    return true;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
