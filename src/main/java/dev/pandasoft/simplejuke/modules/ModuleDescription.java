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

package dev.pandasoft.simplejuke.modules;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ModuleDescription {
    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private String version;
    @JsonProperty(("description"))
    private String description;
    @JsonProperty("authors")
    private List<String> authors;
    @JsonProperty("website")
    private String website;

    @JsonProperty("main")
    private String main;
    @JsonProperty("dependency")
    private List<String> dependency;
    @JsonProperty("loadBefore")
    private List<String> loadBefore;
    @JsonProperty("requiredVersion")
    private String requiredVersion;

    /**
     * @return モジュールの名前
     */
    public String getName() {
        return name;
    }

    /**
     * @return モジュールのバージョン
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return モジュールの説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return モジュールの製作者
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @return モジュールの製作者のウェブサイト
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @return モジュールのメインクラス
     */
    public String getMain() {
        return main;
    }

    /**
     * @return このモジュールが必要とする他のモジュール
     */
    public List<String> getDependency() {
        return dependency;
    }

    /**
     * @return このモジュールがロードされる前にロードするべきモジュール
     */
    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /**
     * @return このモジュールが必要とするSimpleJuke Coreの最低バージョン
     */
    public String getRequiredVersion() {
        return requiredVersion;
    }
}
