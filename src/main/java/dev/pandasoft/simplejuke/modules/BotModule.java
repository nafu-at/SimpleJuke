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
import dev.pandasoft.simplejuke.BotController;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.modules.meta.ModuleDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public abstract class BotModule implements Module {
    private ClassLoader classLoader;
    private ModuleDescription description;
    private File dataFolder;
    private Logger logger;
    private BotBuilder builder;


    public BotModule() {
        classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader))
            throw new IllegalStateException("モジュールは" + ModuleClassLoader.class.getName() + "で読み込まれている必要があります。");
        ((ModuleClassLoader) classLoader).initialize(this);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void registerCommand(CommandExecutor executor) {
        getController().getCommandRegistry().registerCommand(executor, this);
    }

    @Override
    public void registerCommands(List<CommandExecutor> executors) {
        executors.forEach(executor -> registerCommand(executor));
    }

    @Override
    public void removeCommand(CommandExecutor executor) {
        getController().getCommandRegistry().removeCommand(executor, this);
    }

    @Override
    public void removeCommands() {
        getController().getCommandRegistry().removeCommands(this);
    }

    @Override
    public ModuleDescription getDescription() {
        return description;
    }

    @Override
    public BotController getController() {
        return Main.getController();
    }

    @Override
    public InputStream getResources(String filename) {
        return classLoader.getResourceAsStream(filename);
    }

    @Override
    public File getDataFolder() {
        if (!dataFolder.exists())
            dataFolder.mkdirs();
        return dataFolder;
    }

    @Override
    public ModuleClassLoader getClassLoder() {
        return (ModuleClassLoader) classLoader;
    }

    @Override
    public Logger getModuleLogger() {
        return logger;
    }

    /**
     * このメソッドで取得したBotBuilderに登録されている値を変更することによりBotの動作をカスタムすることができます。
     * 取得したBuilderはonLoadメソッド内でのみ使用できそれ以降で行われた変更は適用されません。
     */
    protected BotBuilder getBotBuilder() {
        return builder;
    }

    final void init(ModuleDescription description, BotBuilder builder) {
        this.description = description;
        dataFolder = new File("modules/", description.getName());
        logger = LoggerFactory.getLogger(description.getName());
        this.builder = builder;
    }
}
