/*
 * Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      https://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.spockframework.smoke.extension

import org.spockframework.EmbeddedSpecification
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IDataDriver
import org.spockframework.runtime.model.FeatureInfo

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class SkippableIterations extends EmbeddedSpecification {

  def "can skip iterations"() {
    given:
    runner.addClassImport(SelectIteration)

    when:
    def result = runner.runSpecBody """
    @SelectIteration([1, 3, 4, 6, 9])
    def "data-driven"() {
        expect: true
        where:
        i << (1..10)
    }
"""

    then:
    result.dynamicallyRegisteredCount == 10
    result.containersSucceededCount == 3
    result.testsSkippedCount == 5
    result.testsSucceededCount == 6

  }

  @SelectIteration([1, 3, 4, 6, 9])
  def "data-driven"() {
    expect: true
    where:
    i << (1..10)
  }
}

@Retention(RetentionPolicy.RUNTIME)
@ExtensionAnnotation(SelectIterationExtension)
@interface SelectIteration {
  int[] value()
}

class SelectIterationExtension implements IAnnotationDrivenExtension<SelectIteration> {
  @Override
  void visitFeatureAnnotation(SelectIteration annotation, FeatureInfo feature) {
    Set<Integer> executeIndices = annotation.value() as Set
    feature.setDataDriver { dataIterator, iterationRunner, parameters ->
      {
        int estimatedNumIterations = dataIterator.getEstimatedNumIterations();
        int index = 0;
        while (dataIterator.hasNext()) {
          Object[] arguments = dataIterator.next();
          if (arguments != null) {
            if (executeIndices.contains(index++)) {
              iterationRunner.runIteration(IDataDriver.prepareArgumentArray(arguments, parameters), estimatedNumIterations)
            } else {
              iterationRunner.skipIteration(IDataDriver.prepareArgumentArray(arguments, parameters), estimatedNumIterations, "Not selected")
            }
          }
        }
      }
    }
  }
}
