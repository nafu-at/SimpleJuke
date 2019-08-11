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

package dev.pandasoft.simplejuke.discord.command.executor.module;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.modules.BotModule;
import dev.pandasoft.simplejuke.modules.ModuleRegistry;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ModuleCommand extends CommandExecutor {

    public ModuleCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        ModuleRegistry moduleRegistry = Main.getController().getModuleRegistry();
        if (command.getArgs().length >= 1) {
            try {
                BotModule module;
                switch (command.getArgs()[0].toLowerCase()) {
                    case "enable":
                        module = moduleRegistry.getModule(command.getArgs()[1]);
                        if (module == null) {
                            String filename = command.getArgs()[1];
                            if (!filename.contentEquals(".jar")) {
                                filename += ".jar";
                            }
                            if (Main.getController().getModuleManager().loadModule(new File(filename))) {
                                module = moduleRegistry.getModule(command.getArgs()[1]);
                            } else {
                                return;
                            }
                        }
                        Main.getController().getModuleManager().enableModule(module);
                        command.getChannel().sendMessage(module.getDescription().getName() + "を有効化しました。").queue();
                        break;

                    case "disable":
                        module = moduleRegistry.getModule(command.getArgs()[1]);
                        if (module != null) {
                            Main.getController().getModuleManager().disableModule(module);
                            command.getChannel().sendMessage(module.getDescription().getName() + "を無効化しました。").queue();
                        } else {
                            command.getChannel().sendMessage("このモジュールはロードされていません。").queue();
                        }
                        break;

                    case "load":
                        if (!command.getMessage().getAttachments().isEmpty()) {
                            try {
                                URL url = new URL((command.getMessage().getAttachments().get(0)).getUrl());
                                File file = new File("modules", FilenameUtils.getName(url.getPath()));
                                FileUtils.copyURLToFile(url, file);

                                if (Main.getController().getModuleManager().loadModule(file))
                                    command.getChannel().sendMessage("モジュールをロードしました。使用するには有効化する必要があります。").queue();
                                else
                                    command.getChannel().sendMessage("モジュールのロードに失敗しました。").queue();
                                break;
                            } catch (MalformedURLException e) {
                                ExceptionUtil.sendStackTrace(command.getGuild(), e);
                            } catch (IOException e) {
                                ExceptionUtil.sendStackTrace(command.getGuild(), e, "ファイルのダウンロードに失敗しました。");
                            }
                        }
                }
            } catch (IllegalArgumentException e) {
                command.getChannel().sendMessage("引数が正しくありません。").queue();
            }
        }
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }
}
