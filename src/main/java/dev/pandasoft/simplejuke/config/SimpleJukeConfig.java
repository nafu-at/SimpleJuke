/*
 * Copyright 2019 くまねこそふと.
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

package dev.pandasoft.simplejuke.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleJukeConfig {
    @JsonProperty("basic")
    private BasicConfigSection basic;
    @JsonProperty("advanced")
    private AdvancedConfigSection advanced;
    @JsonProperty("version")
    private String version;

    public BasicConfigSection getBasicConfig() {
        return basic;
    }

    public AdvancedConfigSection getAdvancedConfig() {
        return advanced;
    }

    public String getConfigVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "SimpleJukeConfig{" +
                "basic=" + basic +
                ", advanced=" + advanced +
                ", version='" + version + '\'' +
                '}';
    }
}
