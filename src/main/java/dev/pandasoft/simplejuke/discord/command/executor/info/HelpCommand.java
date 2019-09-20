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

package dev.pandasoft.simplejuke.discord.command.executor.info;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;

public class HelpCommand extends CommandExecutor {
    private static final String HELP_MESSAGE = "`%prefix%help <command>`を使ってコマンドのヘルプを表示することができます。";

    public HelpCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (command.getArgs().length == 0) {
            command.getChannel().sendMessage(
                    HELP_MESSAGE.replaceAll("%prefix%", Main.getController().getConfig().getBasicConfig().getPrefix())).queue();
        } else {
            CommandExecutor executor = Main.getController().getCommandManager().getExecutor(command.getArgs()[0]);
            if (executor == null) {
                command.getChannel().sendMessage(
                        "このコマンドは登録されていません。").queue();
            } else {
                String commandHelp = executor.help();
                if (commandHelp != null)
                    command.getChannel().sendMessage(commandHelp
                            .replaceAll("%prefix%", Main.getController().getConfig().getBasicConfig().getPrefix()))
                            .queue();
                else
                    command.getChannel().sendMessage("このコマンドにはヘルプが登録されていません。").queue();
            }
        }
    }

    @Override
    public String help() {
        return HELP_MESSAGE;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
