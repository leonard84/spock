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

package org.spockframework.runtime;

import org.spockframework.runtime.extension.IDataDriver;
import org.spockframework.runtime.extension.IIterationRunner;
import org.spockframework.runtime.model.ExecutionResult;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import org.spockframework.runtime.model.MethodKind;
import org.spockframework.util.ExceptionUtil;
import spock.config.RunnerConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Adds the ability to run parameterized features.
 *
 * @author Peter Niederwieser
 */
public class PlatformParameterizedSpecRunner extends PlatformSpecRunner {
  public PlatformParameterizedSpecRunner(IRunSupervisor supervisor) {
    super(supervisor);
  }

  @Override
  void runParameterizedFeature(SpockExecutionContext context, ParameterizedFeatureChildExecutor childExecutor) throws InterruptedException {
    if (context.getErrorInfoCollector().hasErrors()) {
      return;
    }

    context = context.withChildStoreProvider();
    FeatureInfo feature = context.getCurrentFeature();
    try (IDataIterator dataIterator = new DataIteratorFactory(supervisor).createFeatureDataIterator(context)) {
      IIterationRunner iterationRunner = createIterationRunner(context, childExecutor);
      IDataDriver dataDriver = feature.getDataDriver();
      dataDriver.runIterations(dataIterator, iterationRunner, feature.getFeatureMethod().getParameters());
      childExecutor.awaitFinished();
    } catch (InterruptedException ie) {
      throw ie;
    } catch (Exception e) {
      ExceptionUtil.sneakyThrow(e);
    } finally {
      runCloseContextStoreProvider(context, MethodKind.CLEANUP);
    }

  }

  private IIterationRunner createIterationRunner(SpockExecutionContext context, ParameterizedFeatureChildExecutor childExecutor) {
    return new IIterationRunner() {
      private final AtomicInteger iterationIndex = new AtomicInteger(0);

      @Override
      public CompletableFuture<ExecutionResult> runIteration(Object[] args, int estimatedNumIterations) {
        Function<IterationNode, CompletableFuture<ExecutionResult>> iThrowableFunction = childExecutor::execute;
        return execute(args, estimatedNumIterations, __ -> {
        });
      }

      @Override
      public CompletableFuture<ExecutionResult> skipIteration(Object[] args, int estimatedNumIterations, String skipReason) {
        return execute(args, estimatedNumIterations, iterationNode -> {
          iterationNode.getIterationInfo().skip(skipReason);
        });
      }

      private CompletableFuture<ExecutionResult> execute(Object[] args, int estimatedNumIterations, Consumer<IterationNode> iterationNodeConfigurer) {
        int currIterationIndex = iterationIndex.getAndIncrement();
        IterationInfo iterationInfo = createIterationInfo(context, currIterationIndex, args, estimatedNumIterations);
        IterationNode iterationNode = new IterationNode(
          context.getParentId().append("iteration", String.valueOf(currIterationIndex)),
          context.getRunContext().getConfiguration(RunnerConfiguration.class), iterationInfo);

        if (context.getErrorInfoCollector().hasErrors()) {
          return CompletableFuture.completedFuture(ExecutionResult.REJECTED);
        }
        if (!iterationInfo.getFeature().getIterationFilter().isAllowed(iterationInfo.getIterationIndex())) {
          iterationNode.getIterationInfo().skip("Filtered by IterationSelector");
        }
        iterationNodeConfigurer.accept(iterationNode);
        return childExecutor.execute(iterationNode);
      }
    };


  }
}
