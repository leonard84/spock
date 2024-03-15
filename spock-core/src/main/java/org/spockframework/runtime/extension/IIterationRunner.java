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

package org.spockframework.runtime.extension;

import org.spockframework.runtime.model.ExecutionResult;
import org.spockframework.util.Beta;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for running an iteration of a test.
 *
 * @since 2.2
 * @author Leonard Brünings
 */
@Beta
public interface IIterationRunner {
  /**
   * Runs the iteration.
   * <p>
   * The returned future can be used to wait for the iteration to complete and to get the result,
   * allowing the data driver to base the next iteration on the result of the previous one.
   * However, it is not required to wait on the futures in any way.
   *
   * @param args arguments to use for the iteration
   * @return a future that will be completed with the result of the iteration
   * @deprecated since 2.4, use {@link #runIteration(Object[], int)} instead
   */
  @Deprecated
  default CompletableFuture<ExecutionResult> runIteration(Object[] args) {
    return runIteration(args, -1);
  }

  /**
   * Runs the iteration.
   * <p>
   * The returned future can be used to wait for the iteration to complete and to get the result,
   * allowing the data driver to base the next iteration on the result of the previous one.
   * However, it is not required to wait on the futures in any way.
   *
   * @param args arguments to use for the iteration
   * @param estimatedNumIterations the estimated number of iterations that will be run. Use -1 if it cannot be determined.
   * @return a future that will be completed with the result of the iteration
   * @since 2.4
   */
  CompletableFuture<ExecutionResult> runIteration(Object[] args, int estimatedNumIterations);

  /**
   * Skips the iteration.
   * <p>
   * Use this if you don't want to execute the iteration, but still want it to be reported.
   *
   * @param args arguments to use for the iteration
   * @param estimatedNumIterations the estimated number of iterations that will be run. Use -1 if it cannot be determined.
   * @param skipReason the reason for this iteration to be skipped
   * @return a future that will be completed with the result of the iteration
   * @since 2.4
   */
  CompletableFuture<ExecutionResult> skipIteration(Object[] args, int estimatedNumIterations, String skipReason);
}
