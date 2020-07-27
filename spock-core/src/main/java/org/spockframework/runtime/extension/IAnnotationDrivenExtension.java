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

package org.spockframework.runtime.extension;

import org.spockframework.runtime.model.*;

import java.lang.annotation.Annotation;

/**
 *
 * @author Peter Niederwieser
 */
public interface IAnnotationDrivenExtension<T extends Annotation> {
  void visitSpecAnnotation(T annotation, SpecInfo spec);
  void visitFeatureAnnotation(T annotation, FeatureInfo feature);
  void visitFixtureAnnotation(T annotation, MethodInfo fixtureMethod);
  void visitFieldAnnotation(T annotation, FieldInfo field);
  void visitSpec(SpecInfo spec);
  default void visitSpecAnnotation(T[] annotations, SpecInfo spec){
    for (T annotation : annotations) {
      visitSpecAnnotation(annotation, spec);
    }
  }
  default void visitFeatureAnnotation(T[] annotations, FeatureInfo feature) {
    for (T annotation : annotations) {
      visitFeatureAnnotation(annotation, feature);
    }
  }
  default void visitFixtureAnnotation(T[] annotations, MethodInfo fixtureMethod) {
    for (T annotation : annotations) {
      visitFixtureAnnotation(annotation, fixtureMethod);
    }
  }
  default void visitFieldAnnotation(T[] annotations, FieldInfo field) {
    for (T annotation : annotations) {
      visitFieldAnnotation(annotation, field);
    }
  }
}
