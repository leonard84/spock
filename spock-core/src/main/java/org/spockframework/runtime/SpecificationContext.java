/*
 * Copyright 2023 the original author or authors.
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

import org.spockframework.lang.ISpecificationContext;
import org.spockframework.mock.IMockController;
import org.spockframework.mock.runtime.MockController;
import org.spockframework.runtime.extension.IStore;
import org.spockframework.runtime.extension.IStoreProvider;
import org.spockframework.runtime.model.*;
import spock.lang.Specification;

public class SpecificationContext implements ISpecificationContext {
  private volatile SpecInfo currentSpec;
  private volatile FeatureInfo currentFeature;
  private volatile IterationInfo currentIteration;

  private volatile IStoreProvider storeProvider;
  private volatile Specification sharedInstance;

  private volatile Throwable thrownException;

  private final IMockController mockController = new MockController();

  public static final String GET_SHARED_INSTANCE = "getSharedInstance";
  public Specification getSharedInstance() {
    return sharedInstance;
  }

  public void setSharedInstance(Specification sharedInstance) {
    this.sharedInstance = sharedInstance;
  }

  @Override
  public SpecInfo getCurrentSpec() {
    return currentSpec;
  }

  public void setCurrentSpec(SpecInfo currentSpec) {
    this.currentSpec = currentSpec;
  }

  @Override
  public FeatureInfo getCurrentFeature() {
    if (currentFeature == null) {
      throw new IllegalStateException("Cannot request current feature in @Shared context");
    }
    return currentFeature;
  }

  public void setCurrentFeature(FeatureInfo currentFeature) {
    this.currentFeature = currentFeature;
  }

  @Override
  public IterationInfo getCurrentIteration() {
    if (currentIteration == null) {
      throw new IllegalStateException("Cannot request current iteration in @Shared context, or feature context");
    }
    return currentIteration;
  }

  public void setCurrentIteration(IterationInfo currentIteration) {
    this.currentIteration = currentIteration;
  }

  @Override
  public Throwable getThrownException() {
    return thrownException;
  }

  public static String SET_THROWN_EXCEPTION = "setThrownException";
  public void setThrownException(Throwable exception) {
    thrownException = exception;
  }

  public static String GET_MOCK_CONTROLLER = "getMockController";
  @Override
  public IMockController getMockController() {
    return mockController;
  }

  @Override
  public IStore getStore(IStore.Namespace namespace) {
    if (storeProvider == null) {
      throw new IllegalStateException("Cannot request store provider in this context.");
    }
    return storeProvider.getStore(namespace);
  }

  public void setStoreProvider(IStoreProvider storeProvider) {
    this.storeProvider = storeProvider;
  }

  public IStoreProvider getStoreProvider() {
    return storeProvider;
  }
}
