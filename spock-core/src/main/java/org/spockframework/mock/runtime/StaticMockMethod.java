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

package org.spockframework.mock.runtime;

import io.leangen.geantyref.GenericTypeReflector;
import org.spockframework.mock.IMockMethod;
import org.spockframework.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public class StaticMockMethod implements IMockMethod {
  private final Method method;
  private final Type mockType;
  private final List<Class<?>> additionalInterfaces;

  public StaticMockMethod(Method method, Type mockType, List<Class<?>> additionalInterfaces) {
    this.method = method;
    this.mockType = mockType;
    this.additionalInterfaces = additionalInterfaces;
  }

  @Override
  public String getName() {
    return method.getName();
  }

  @Override
  public List<Class<?>> getParameterTypes() {
    return ReflectionUtil.eraseTypes(getExactParameterTypes());
  }

  @Override
  public List<Type> getExactParameterTypes() {
    try {
      return ReflectionUtil.getResolvedParameterTypes(method, mockType);
    } catch (IllegalArgumentException e) {
      for (Class<?> additionalInterface : additionalInterfaces) {
        try {
          return ReflectionUtil.getResolvedParameterTypes(method, additionalInterface);
        } catch (IllegalArgumentException e1) {
          e.addSuppressed(e1);
        }
      }
      throw e;
    }
  }

  @Override
  public Class<?> getReturnType() {
    return GenericTypeReflector.erase(getExactReturnType());
  }

  @Override
  public Type getExactReturnType() {
    try {
      return ReflectionUtil.getResolvedReturnType(method, mockType);
    } catch (IllegalArgumentException e) {
      for (Class<?> additionalInterface : additionalInterfaces) {
        try {
          return ReflectionUtil.getResolvedReturnType(method, additionalInterface);
        } catch (IllegalArgumentException e1) {
          e.addSuppressed(e1);
        }
      }
      throw e;
    }
  }

  @Override
  public boolean isStatic() {
    return Modifier.isStatic(method.getModifiers());
  }

  @Override
  public String toString() {
    return "StaticMockMethod{" +
      "mockType=" + mockType +
      ", method=" + method +
      ", additionalInterfaces=" + additionalInterfaces +
      '}';
  }
}
