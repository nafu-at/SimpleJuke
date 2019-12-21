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

package dev.pandasoft.simplejuke.discord.command.executor.music.control;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.database.legacy.entities.GuildSettings;
import dev.pandasoft.simplejuke.database.legacy.entities.RepeatSetting;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class RepeatCommand extends CommandExecutor {

    public RepeatCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (command.getArgs().length >= 1) {
            try {
                GuildSettings settings = Main.getController().getGuildSettingsTable().loadSettings(command.getGuild());
                RepeatSetting repeatSetting;
                switch (command.getArgs()[0].toLowerCase()) {
                    case "all":
                    case "queue":
                        repeatSetting = RepeatSetting.ALL;
                        break;

                    case "single":
                    case "track":
                        repeatSetting = RepeatSetting.SINGLE;
                        break;

                    case "off":
                    case "none":
                    default:
                        repeatSetting = RepeatSetting.NONE;
                        break;
                }
                settings.setRepeat(repeatSetting);
                Main.getController().getGuildSettingsTable().saveSettings(command.getGuild(), settings);
                if (!settings.getRepeat().equals(RepeatSetting.NONE))
                    command.getChannel().sendMessage("リピートを設定しました。").queue();
                else
                    command.getChannel().sendMessage("リピートを解除しました。").queue();
            } catch (SQLException | IOException e) {
                log.error("ギルド固有設定の保存中にエラーが発生しました。", e);
            } catch (IllegalArgumentException e) {
                command.getChannel().sendMessage("値が正しくありません。single, allのどちらかを選択してください").queue();
            }
        } else {
            GuildSettings settings = Main.getController().getGuildSettingsTable().loadSettings(command.getGuild());
            command.getChannel().sendMessage("現在のリピート設定は**" + settings.getRepeat().toString() + "**です。").queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%repeat off リピート設定を解除します。\n" +
                "%prefix%repeat single 再生中の楽曲をリピートします。\n" +
                "%prefix%repeat all 全てのキューをリピートします。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
