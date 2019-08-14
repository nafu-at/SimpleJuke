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

package dev.pandasoft.simplejuke.discord.handler;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.database.entities.GuildSettings;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.BotCommandParser;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

@Slf4j
public class MessageReceivedEventHandler extends ListenerAdapter {
    private final BotCommandParser parser = new BotCommandParser();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() == event.getJDA().getSelfUser() ||
                event.getAuthor().isBot() ||
                !event.getTextChannel().canTalk())
            return;

        Guild guild = event.getGuild();
        GuildSettings guildSettings = Main.getController().getGuildSettingsManager().loadSettings(guild);
        String prefix = guildSettings.getPrefix();
        BotCommand command = parser.parse(prefix, event);
        if (command == null)
            return;
        log.debug("Command Received: {}", command.toString());

        if (command.getCommand().getPermission().getPermissionLevel() >
                Main.getController().getUserDataManager().getUserPermission(event.getGuild().getMember(event.getAuthor())).getPermissionLevel()) {
            event.getChannel().sendMessage("このコマンドを実行するために必要な権限がありません！").queue();
            return;
        }

        try {
            command.getCommand().onInvoke(command);
        } catch (Exception e) {
            ExceptionUtil.sendStackTrace(event.getGuild(), e, "コマンドの実行に失敗しました。");
        }
        MessageUtil.cacheChannel(event.getGuild(), event.getChannel());
    }
}
