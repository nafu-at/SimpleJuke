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

import dev.pandasoft.simplejuke.modules.exception.ModuleDuplicateException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleRegistry {
    private static final Map<String, BotModule> modules = new LinkedHashMap<>();

    private ModuleRegistry() {
        throw new UnsupportedOperationException();
    }

    /**
     * モジュールを登録します。
     *
     * @param module 登録するモジュール
     * @throws ModuleDuplicateException 登録しようとしているモジュールが既に存在している場合にスローされます。
     */
    public static synchronized void registerModule(BotModule module) throws ModuleDuplicateException {
        if (modules.containsKey(module.getDescription().getName()))
            throw new ModuleDuplicateException(module.getDescription().getName());
        else
            modules.put(module.getDescription().getName(), module);
    }

    /**
     * 登録されているモジュールを削除します。
     *
     * @param name 削除するモジュール
     */
    public static synchronized void deleteModule(String name) {
        modules.remove(name);
    }

    /**
     * 登録されている全てのモジュールの一覧を返します。
     *
     * @return 登録されている全てのモジュールの一覧
     */
    public static synchronized List<BotModule> getModules() {
        return new ArrayList<>(modules.values());
    }

    /**
     * 名前から対応するモジュールを取得します。
     *
     * @param name 取得するモジュールの名前
     * @return 該当するモジュール
     */
    public static synchronized BotModule getModule(String name) {
        return modules.get(name);
    }
}
