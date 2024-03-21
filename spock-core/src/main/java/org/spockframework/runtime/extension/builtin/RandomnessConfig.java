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

import spock.config.ConfigurationObject;

import java.util.Optional;

@ConfigurationObject("randomness")
public class RandomnessConfig {
  public static final String SEED_KEY = "spock.randomness.seed";
  Object seed = Optional.of((Object) System.getProperty(SEED_KEY)).orElseGet(System::currentTimeMillis);
  boolean printSeedValueOnStart = false;
  boolean printSeedValueOnFirstUse = true;
}
