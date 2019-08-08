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

import dev.pandasoft.simplejuke.BotBuilder;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.modules.exception.InvalidDescriptionException;
import dev.pandasoft.simplejuke.modules.exception.InvalidModuleException;
import dev.pandasoft.simplejuke.modules.exception.ModuleDuplicateException;
import dev.pandasoft.simplejuke.modules.exception.UnknownDependencyException;
import dev.pandasoft.simplejuke.modules.meta.ModuleDescription;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ModuleManager {
    private final ModuleLoader moduleLoader;

    private List<File> files;

    public ModuleManager(BotBuilder builder) {
        moduleLoader = new ModuleLoader(builder);
        files = moduleLoader.searchModules();
    }

    public void loadAllModules() {
        while (!files.isEmpty()) {
            Iterator<File> iterator = moduleLoader.searchModules().iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                if (!file.getPath().endsWith(".jar")) {
                    files.remove(file);
                    continue;
                }
                ModuleDescription description;
                try {
                    description = moduleLoader.loadModuleDescription(file);
                } catch (InvalidModuleException | InvalidDescriptionException e) {
                    log.error("モジュールのロードに失敗しました。", e);
                    files.remove(file);
                    continue;
                }

                if (description.getLoadBefore() != null && !description.getLoadBefore().isEmpty()) {
                    for (String module : description.getLoadBefore()) {
                        if (ModuleRegistry.getModule(module) == null && files.size() != 1) {
                            continue;
                        }
                    }
                }

                loadModule(file);
                files.remove(file);
            }
        }
    }

    /**
     * モジュールをロードします。
     *
     * @param file モジュールJar
     * @return モジュールのロードに成功した場合true
     */
    public boolean loadModule(File file) {
        BotModule module = null;
        try {
            module = moduleLoader.loadModule(file);
        } catch (InvalidModuleException e) {
            log.error("モジュールの形式が正しくないため読み込めませんでした。", e);
            return false;
        } catch (UnknownDependencyException e) {
            log.error("依存関係が解決できなかったため正しく読み込めませんでした。", e);
            return false;
        }

        if (module == null)
            return false;

        try {
            ModuleRegistry.registerModule(module);
        } catch (ModuleDuplicateException e) {
            log.warn("{} 既に同じモジュールがロードされています。", module.getDescription().getName());
            return false;
        }

        try {
            module.onLoad();
            log.info("{} がロードされました。", module.getDescription().getName());
        } catch (Throwable e) {
            log.error("{} のロードに失敗しました。", module.getDescription().getName(), e);
            ModuleRegistry.deleteModule(module.getDescription().getName());
            return false;
        }
        return true;
    }

    public void enableAllModules() {
        for (BotModule module : ModuleRegistry.getModules()) {
            enableModule(module);
        }
    }

    /**
     * モジュールを有効化します。
     *
     * @param name 有効化するモジュールの名
     * @return モジュールの有効化に成功した場合にtrueを返します。
     */
    public boolean enableModule(String name) {
        BotModule module = ModuleRegistry.getModule(name);
        return enableModule(module);
    }

    /**
     * モジュールを有効化します。
     *
     * @param module 有効化するモジュール
     * @return モジュールの有効化に成功した場合にtrueを返します。
     */
    public boolean enableModule(Module module) {
        try {
            module.onEnable();
            log.info("{} が有効化されました。", module.getDescription().getName());
        } catch (Throwable e) {
            log.error("モジュールの有効化中にエラーが発生しました。: {}\n", module.getDescription().getName(), e);
            return false;
        }
        return true;
    }

    public void disableAllModules() {
        for (BotModule module : ModuleRegistry.getModules()) {
            disableModule(module);
        }
    }

    /**
     * モジュールをアンロードします。
     *
     * @param name アンロードするモジュール名
     * @return モジュールの無効化に成功した場合にtrueを返します。
     */
    public boolean disableModule(String name) {
        BotModule module = ModuleRegistry.getModule(name);
        return disableModule(module);
    }

    /**
     * モジュールをアンロードします。
     *
     * @param module アンロードするモジュール
     * @return モジュールの無効化に成功した場合にtrueを返します。
     */
    public boolean disableModule(BotModule module) {
        try {
            module.onDisable();
            ClassLoader classLoader = this.getClass().getClassLoader();
            if (classLoader instanceof ModuleClassLoader)
                ((ModuleClassLoader) classLoader).close();
            Main.getController().getCommandRegistry().removeCommands(module);
            ModuleRegistry.deleteModule(module.getDescription().getName());
            log.info("{} が無効化されました。", module.getDescription().getName());
        } catch (Throwable e) {
            log.error("モジュールの無効化中にエラーが発生しました。: {}\n", module.getDescription().getName(), e);
            return false;
        }
        try {
            module.getClassLoder().close();
        } catch (IOException e) {
            log.error("モジュールクラスローダーの終了に失敗しました。", e);
            return false;
        }
        return true;
    }
}
