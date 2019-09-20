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

package dev.pandasoft.simplejuke.discord.command.executor.admin;

import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.discord.command.CommandTempRegistry;
import org.apache.commons.lang3.RandomStringUtils;

public class ShutdownCommand extends CommandExecutor {
    private final CommandTempRegistry tempRegistry;

    public ShutdownCommand(String name, String... aliases) {
        super(name, aliases);
        tempRegistry = new CommandTempRegistry();
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (command.getArgs().length == 0) {
            String pass = RandomStringUtils.randomAlphanumeric(6);
            tempRegistry.registerTemp(command.getGuild(), pass);
            command.getChannel().sendMessage("誤操作防止のため実行キーが生成されました。\n" +
                    "**`" + command.getTrigger() + " " + pass + "`**を指定して実行して下さい。").queue();
        } else {
            if (command.getArgs()[0].equals(tempRegistry.getTempObject(command.getGuild()).get(0)))
                System.exit(0);
            else
                command.getChannel().sendMessage("実行キーが正しくありません").queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%shutdown Botを終了します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }
}
