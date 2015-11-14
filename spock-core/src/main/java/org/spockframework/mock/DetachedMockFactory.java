package org.spockframework.mock;

import groovy.lang.Closure;
import org.spockframework.lang.MockFactory;
import org.spockframework.util.Beta;
import org.spockframework.util.Nullable;
import spock.lang.Specification;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This factory allows the creations of mocks outside of a {@link spock.lang.Specification},
 * e.g., in a Spring configuration.
 * <p/>
 * In order to be usable those Mocks must be manually attached to the {@link spock.lang.Specification}
 * using {@link MockUtil#attachMock(Object, Specification)} and detached afterwards {@link MockUtil#detachMock(Object)}.
 */
@Beta
public class DetachedMockFactory implements MockFactory {
  @Override
  public <T> T Mock(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap());
  }

  @Override
  public <T> T Mock(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      options);
  }

  @Override
  public <T> T Stub(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap());
  }

  @Override
  public <T> T Stub(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      options);
  }

  @Override
  public <T> T Spy(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap());
  }

  @Override
  public <T> T Spy(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      options);
  }


  @SuppressWarnings("unchecked")
  public <T> T createMock(@Nullable String name, Class<T> type, MockNature nature,
                          MockImplementation implementation, Map<String, Object> options) {
    ClassLoader classLoader = type.getClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    return (T) new MockUtil().createDetachedMock(name, type, nature, implementation, options, classLoader);
  }

  private String inferNameFromType(Class<?> type) {
    return type.getSimpleName();
  }
}
