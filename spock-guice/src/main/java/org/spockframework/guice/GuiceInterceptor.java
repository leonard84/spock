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

package org.spockframework.guice;

import org.spockframework.mock.*;
import org.spockframework.runtime.extension.*;
import org.spockframework.runtime.model.*;
import spock.guice.GuiceSpyInterceptor;
import spock.lang.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.*;
import com.google.inject.spi.InjectionPoint;

/**
 * Creates a Guice injector, and injects Guice-provided objects into specifications.
 *
 * @author Peter Niederwieser
 */
// Important implementation detail: Only the fixture methods of
// spec.getTopSpec() are intercepted (see GuiceExtension)
public class GuiceInterceptor extends AbstractMethodInterceptor {
  private static final MockUtil MOCK_UTIL = new MockUtil();
  private final Set<Class<? extends Module>> moduleClasses;
  private final Set<InjectionPoint> injectionPoints;
  private final Set<FieldInfo> spyFields;

  private Injector injector;

  public GuiceInterceptor(SpecInfo spec, Set<Class<? extends Module>> moduleClasses) {
    this.moduleClasses = moduleClasses;
    injectionPoints = InjectionPoint.forInstanceMethodsAndFields(spec.getReflection());
    spyFields = spec.getAllFields().stream().filter(field -> field.isAnnotationPresent(GuiceSpyInterceptor.class)).collect(Collectors.toSet());
  }

  @Override
  public void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
    createInjector();
    injectValues(invocation.getSharedInstance(), true, (Specification)invocation.getInstance());
    invocation.proceed();
  }

  @Override
  public void interceptInitializerMethod(IMethodInvocation invocation) throws Throwable {
    injectValues(invocation.getInstance(), false, (Specification)invocation.getInstance());
    invocation.proceed();
  }

  private void createInjector() {
    injector = Guice.createInjector(createModules());
  }

  @SuppressWarnings("rawtypes")
  private List<Module> createModules() {
    List<Module> modules = new ArrayList<>();
    for (Class<? extends Module> clazz : moduleClasses) {
      try {
        modules.add(clazz.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new GuiceExtensionException("Failed to instantiate module '%s'", e).withArgs(clazz.getSimpleName());
      }
    }
    if (!spyFields.isEmpty()) {
      Map<String, Class> classes = spyFields.stream().collect(Collectors.toMap(FieldInfo::getName, FieldInfo::getType));
      modules.add(new SpyInterceptorModule(classes));
    }
    return modules;
  }

  private void injectValues(Object target, boolean sharedFields, Specification specInstance) throws IllegalAccessException {
    for (InjectionPoint point : injectionPoints) {
      if (!(point.getMember() instanceof Field))
        throw new GuiceExtensionException("Method injection is not supported; use field injection instead");

      Field field = (Field)point.getMember();
      if (field.isAnnotationPresent(Shared.class) != sharedFields) continue;

      Object value = injector.getInstance(point.getDependencies().get(0).getKey());
      if (MOCK_UTIL.isMock(value)) {
        MOCK_UTIL.attachMock(value, specInstance);
      }
      field.setAccessible(true);
      field.set(target, value);
    }

    MockHolder mockHolder = new MockHolder();
    injector.injectMembers(mockHolder);
    if (mockHolder.hasMocks()) {
      mockHolder.attachAll(specInstance);
      for (FieldInfo field : spyFields) {
        field.writeValue(target, mockHolder.getMockFor(field.getType()));
      }
    } else {
      if (!spyFields.isEmpty()) {
        throw new GuiceExtensionException("No mock objects available for injection");
      }
    }
  }

  private static class MockHolder {
    @Inject
    public Set<ISpockMockObject> mockObjects;

    boolean hasMocks() {
      return mockObjects != null && !mockObjects.isEmpty();
    }

    void attachAll(Specification spec) {
      mockObjects.forEach(mock -> MOCK_UTIL.attachMock(mock, spec));
    }

    Object getMockFor(Class<?> clazz) {
      return mockObjects.stream()
        .filter(clazz::isInstance)
        .findFirst()
        .orElseThrow(() -> new GuiceExtensionException("No mock found for class " + clazz.getName()));
    }
  }
}
