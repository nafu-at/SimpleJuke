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

package dev.pandasoft.simplejuke.discord.command.executor.config;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.database.legacy.entities.GuildSettings;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class ConfigCommand extends CommandExecutor {

    public ConfigCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildSettings settings = Main.getController().getGuildSettingsTable().loadSettings(command.getGuild());
        try {
            if (command.getArgs().length == 0) {
                sendNowSettings(command, settings);
            } else switch (command.getArgs()[0].toLowerCase()) {
                case "prefix":
                    if (command.getArgs().length >= 2)
                        settings.setPrefix(command.getArgs()[1]);
                    else
                        settings.setPrefix(Main.getController().getConfig().getBasicConfig().getPrefix());
                    command.getChannel().sendMessage("コマンド接頭辞を" + settings.getPrefix() + "に設定しました。").queue();
                    break;

                case "announce":
                    if (command.getArgs().length >= 2)
                        settings.setAnnounce(Boolean.parseBoolean(command.getArgs()[1]));
                    command.getChannel().sendMessage("再生開始アナウンスを" + settings.isAnnounce() +
                            "に設定しました。").queue();
                    break;

                case "autoleave":
                    if (command.getArgs().length >= 2)
                        settings.setAutoLeave(Boolean.parseBoolean(command.getArgs()[1]));
                    command.getChannel().sendMessage("自動退出を" + settings.isAutoLeave() +
                            "に設定しました。").queue();
                    break;

                case "listsize":
                case "listrange":
                    if (command.getArgs().length >= 2) {
                        int range = Integer.parseInt(command.getArgs()[1]);
                        if (range >= 5 && range <= 25) {
                            settings.setListRange(range);
                            command.getChannel().sendMessage("キューリストの表示項目数を" + settings.getListRange() +
                                    "に設定しました。").queue();
                        } else {
                            command.getChannel().sendMessage("範囲は [5-25] の間で指定して下さい。").queue();
                        }
                    }
                    break;

                default:
                    sendNowSettings(command, settings);
                    break;
            }

            Main.getController().getGuildSettingsTable().saveSettings(command.getGuild(), settings);
        } catch (SQLException | IOException e) {
            ExceptionUtil.sendStackTrace(command.getGuild(), e, "ギルド固有設定の保存中にエラーが発生しました。");
            log.error("ギルド固有設定の保存中にエラーが発生しました。", e);
        } catch (IllegalArgumentException e) {
            command.getChannel().sendMessage("指定された値が正しくありません！").queue();
        }
    }

    private void sendNowSettings(BotCommand command, GuildSettings settings) {
        StringBuilder sb = new StringBuilder();
        sb.append("このギルドの設定は以下の通りです。\n```");
        sb.append("prefix: " + settings.getPrefix() + "\n");
        sb.append("Volume: " + settings.getVolume() + "\n");
        sb.append("Repeat: " + settings.getRepeat().name() + "\n");
        sb.append("Shuffle: " + settings.isShuffle() + "\n");
        sb.append("AutoLeave: " + settings.isAutoLeave() + "\n");
        sb.append("StartAnnounce: " + settings.isAnnounce() + "\n");
        sb.append("ListRange: " + settings.getListRange() + "\n");
        sb.append("```");
        command.getChannel().sendMessage(sb.toString()).queue();
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.GUILD_OWNER;
    }
}
