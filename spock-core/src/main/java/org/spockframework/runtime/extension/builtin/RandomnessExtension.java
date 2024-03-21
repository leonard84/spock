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

package org.spockframework.runtime.extension.builtin;

import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.extension.ISpockExecution;
import org.spockframework.runtime.extension.IStore;
import org.spockframework.util.Checks;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class RandomnessExtension implements IGlobalExtension {
  private static final IStore.Namespace NAMESPACE = IStore.Namespace.create(RandomnessExtension.class);

  private final RandomnessConfig config;

  public RandomnessExtension(RandomnessConfig config) {
    this.config = config;
  }


  long getSeedValue() {
    Object seed = config.seed;
    Checks.notNull(seed, () -> "seed must not be null");
    if (seed instanceof Number) {
      return ((Number) seed).longValue();
    }
    String stringRepresentation = String.valueOf(seed);
    try {
      return Long.parseLong(stringRepresentation);
    } catch (NumberFormatException ignore) {
    }
    return stringRepresentation.hashCode();
  }

  @Override
  public void executionStart(ISpockExecution spockExecution) {
    long seedValue = getSeedValue();
    if (config.printSeedValueOnStart) {
      printSeedValue(seedValue);
      spockExecution.getStore(NAMESPACE).put("randomnessSource", ((RandomnessSource) () -> new Random(seedValue)));
    } else if (config.printSeedValueOnFirstUse) {
      AtomicBoolean printed = new AtomicBoolean();
      spockExecution.getStore(NAMESPACE).put("randomnessSource", ((RandomnessSource) () -> {
        if (printed.compareAndSet(false, true)) {
          printSeedValue(seedValue);
        }
        return new Random(seedValue);
      }));
    } else {
      spockExecution.getStore(NAMESPACE).put("randomnessSource", ((RandomnessSource) () -> new Random(seedValue)));
    }

  }

  private static void printSeedValue(long seedValue) {
    System.err.printf("Spock Randomness using seed=%d you can rerun with -D%s=%d%n", seedValue, RandomnessConfig.SEED_KEY, seedValue);
  }

  public static RandomnessSource getRandomnessSupplier(IMethodInvocation methodInvocation) {
    return methodInvocation.getStore(NAMESPACE).get("randomnessSource", RandomnessSource.class);
  }

  @FunctionalInterface
  public interface RandomnessSource extends Supplier<Random> {
  }
}
