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

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.ExclusiveResource;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import spock.config.RunnerConfiguration;

import java.util.Set;

import static java.util.Collections.emptySet;

public class IterationNode extends SpockNode<FeatureInfo> {
  private final IterationInfo iterationInfo;

  protected IterationNode(UniqueId uniqueId, RunnerConfiguration configuration, IterationInfo iterationInfo) {
    super(uniqueId, iterationInfo.getDisplayName(), featureToMethodSource(iterationInfo.getFeature()), configuration,
      iterationInfo.getFeature());
    this.iterationInfo = iterationInfo;
  }

  @Override
  public SpockExecutionContext prepare(SpockExecutionContext context) throws Exception {
    if (iterationInfo.getFeature().isSkipped()) {
      // Node.prepare is called before Node.shouldBeSkipped, so we just skip the prepare step.
      return context;
    }
    context.getRunContext().ensureInstalled();
    context.getErrorInfoCollector().assertEmpty();
    context = context.withCurrentIteration(iterationInfo);
    context = context.getRunner().createSpecInstance(context, false);
    context.getRunner().runInitializer(context);
    context.getErrorInfoCollector().assertEmpty();
    return context;
  }

  @Override
  public SpockExecutionContext before(SpockExecutionContext context) throws Exception {
    ErrorInfoCollector errorInfoCollector = new ErrorInfoCollector();
    context = context.withErrorInfoCollector(errorInfoCollector);
    context.getRunner().runSetup(context);
    errorInfoCollector.assertEmpty();
    return context;
  }

  @Override
  public SpockExecutionContext execute(SpockExecutionContext context, DynamicTestExecutor dynamicTestExecutor) throws Exception {
    verifyNotSkipped(iterationInfo.getFeature());
    ErrorInfoCollector errorInfoCollector = new ErrorInfoCollector();
    context = context.withErrorInfoCollector(errorInfoCollector);
    context.getRunner().runFeatureMethod(context);
    errorInfoCollector.assertEmpty();
    return context;
  }

  @Override
  public void after(SpockExecutionContext context) throws Exception {
    ErrorInfoCollector errorInfoCollector = new ErrorInfoCollector();
    context = context.withErrorInfoCollector(errorInfoCollector);
    context.getRunner().runCleanup(context);
    errorInfoCollector.assertEmpty();
  }

  @Override
  public void around(SpockExecutionContext context, Invocation<SpockExecutionContext> invocation) {
    ErrorInfoCollector errorInfoCollector = new ErrorInfoCollector();
    SpockExecutionContext innerContext = context.withErrorInfoCollector(errorInfoCollector);
    innerContext.getRunner().runIteration(innerContext, iterationInfo, () -> sneakyInvoke(invocation, innerContext));
    errorInfoCollector.assertEmpty();
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }

  @Override
  public SkipResult shouldBeSkipped(SpockExecutionContext context) {
    return shouldBeSkipped(iterationInfo);
  }

  @Override
  public Set<ExclusiveResource> getExclusiveResources() {
    return emptySet();
  }

  public IterationInfo getIterationInfo() {
    return iterationInfo;
  }
}
