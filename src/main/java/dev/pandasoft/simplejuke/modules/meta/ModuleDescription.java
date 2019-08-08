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

package dev.pandasoft.simplejuke.modules.meta;

import java.util.List;

public class ModuleDescription {

    private String name;
    private String version;
    private String description;
    private List<String> authors;
    private String website;

    private String main;
    private List<String> dependency;
    private List<String> loadBefore;
    private String requiredVersion;

    /**
     * モジュールの名前を取得します。
     *
     * @return モジュールの名前
     */
    public String getName() {
        return name;
    }

    /**
     * モジュールのバージョンを取得します。
     *
     * @return モジュールのバージョン
     */
    public String getVersion() {
        return version;
    }

    /**
     * モジュールの説明を取得します。
     *
     * @return モジュールの説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * モジュールの製作者を取得します。
     *
     * @return モジュールの製作者
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * モジュールの製作者のウェブサイトを取得します。
     *
     * @return モジュールの製作者のウェブサイト
     */
    public String getWebsite() {
        return website;
    }

    /**
     * モジュールのメインクラスを取得します。
     *
     * @return モジュールのメインクラス
     */
    public String getMain() {
        return main;
    }

    /**
     * このモジュールが必要とする他のモジュールのリストを取得します。
     *
     * @return このモジュールが必要とする他のモジュール
     */
    public List<String> getDependency() {
        return dependency;
    }

    /**
     * このモジュールがロードされる前にロードするべきモジュールのリストを取得します。
     *
     * @return このモジュールがロードされる前にロードするべきモジュール
     */
    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /**
     * このモジュールが必要とするButtonBotNextの最低バージョンを取得します。
     *
     * @return このモジュールが必要とするButtonBotNextの最低バージョン
     */
    public String getRequiredVersion() {
        return requiredVersion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setDependency(List<String> dependency) {
        this.dependency = dependency;
    }

    public void setLoadBefore(List<String> loadBefore) {
        this.loadBefore = loadBefore;
    }

    public void setRequiredVersion(String requiredVersion) {
        this.requiredVersion = requiredVersion;
    }
}
