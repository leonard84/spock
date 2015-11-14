package org.spockframework.mock;

import groovy.lang.Closure;
import org.spockframework.lang.MockFactory;
import org.spockframework.mock.runtime.CompositeMockFactory;
import org.spockframework.mock.runtime.MockConfiguration;
import org.spockframework.runtime.GroovyRuntimeUtil;
import org.spockframework.util.Nullable;
import spock.lang.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This factory allows the creations of mocks outside of a {@link spock.lang.Specification},
 * e.g., in a Spring configuration.
 *
 * In order to be usable those Mocks must be manually attached to the {@link spock.lang.Specification}
 * using {@link MockUtil#attachMock(Object, Specification)} and detached afterwards {@link MockUtil#detachMock(Object)}.
 */
public class DetachedMockFactory implements MockFactory {
  @Override
  public <T> T Mock(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), null);
  }

  @Override
  public <T> T Mock(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      options, null);
  }

  @Override
  public <T> T Mock(Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), interactions);
  }

  @Override
  public <T> T Mock(Map<String, Object> options, Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.MOCK, MockImplementation.JAVA,
      options, interactions);
  }

  @Override
  public <T> T Stub(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), null);
  }

  @Override
  public <T> T Stub(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      options, null);
  }

  @Override
  public <T> T Stub(Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), interactions);
  }

  @Override
  public <T> T Stub(Map<String, Object> options, Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.STUB, MockImplementation.JAVA,
      options, interactions);
  }

  @Override
  public <T> T Spy(Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), null);
  }

  @Override
  public <T> T Spy(Map<String, Object> options, Class<T> type) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      options, null);
  }

  @Override
  public <T> T Spy(Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      Collections.<String, Object>emptyMap(), interactions);
  }

  @Override
  public <T> T Spy(Map<String, Object> options, Class<T> type, Closure interactions) {
    return createMock(inferNameFromType(type), type, MockNature.SPY, MockImplementation.JAVA,
      options, interactions);
  }

  @SuppressWarnings("unchecked")
  public <T> T createMock(@Nullable String name, Class<T> type, MockNature nature,
                           MockImplementation implementation, Map<String, Object> options, @Nullable Closure closure) {
    ClassLoader classLoader = type.getClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    if (closure != null) {
      options = new HashMap<String, Object>(options);
      options.put("initializationClosure", closure);
    }
    return (T) new MockUtil().createDetachedMock(name, type, nature, implementation, options, classLoader);
  }

  private String inferNameFromType(Class<?> type) {
    return type.getSimpleName();
  }
}
