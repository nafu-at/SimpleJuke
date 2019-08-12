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

import dev.pandasoft.simplejuke.BotController;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public abstract class BotModule implements Module {
    private final ClassLoader classLoader;
    private final CommandRegistry commandRegistry;
    private ModuleDescription description;
    private File dataFolder;
    private Logger logger;

    public BotModule() {
        classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader))
            throw new IllegalStateException("モジュールは" + ModuleClassLoader.class.getName() + "で読み込まれている必要があります。");
        ((ModuleClassLoader) classLoader).initialize(this);
        commandRegistry = new CommandRegistry();
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
        commandRegistry.registerCommand(executor);
    }

    @Override
    public void registerCommands(List<CommandExecutor> executors) {
        executors.forEach(this::registerCommand);
    }

    @Override
    public void removeCommand(CommandExecutor executor) {
        commandRegistry.removeCommand(executor);
    }

    @Override
    public void removeCommands() {
        commandRegistry.removeCommands();
    }

    @Override
    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
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

    final void init(ModuleDescription description) {
        this.description = description;
        dataFolder = new File("modules/", description.getName());
        logger = LoggerFactory.getLogger(description.getName());
    }
}
