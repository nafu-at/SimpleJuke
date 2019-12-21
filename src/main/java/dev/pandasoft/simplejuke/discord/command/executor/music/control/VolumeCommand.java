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
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.database.legacy.entities.GuildSettings;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class VolumeCommand extends CommandExecutor {

    public VolumeCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (command.getArgs().length != 0) {
            try {
                int volume = Integer.parseInt(command.getArgs()[0]);
                GuildSettings settings = Main.getController().getGuildSettingsTable().loadSettings(command.getGuild());
                audioPlayer.setVolume(volume);
                settings.setVolume(volume);
                Main.getController().getGuildSettingsTable().saveSettings(command.getGuild(), settings);
            } catch (NumberFormatException e) {
                command.getChannel().sendMessage("指定された値が正しくありません！").queue();
            } catch (SQLException | IOException e) {
                ExceptionUtil.sendStackTrace(command.getGuild(), e, "ギルド固有設定の保存中にエラーが発生しました。");
                log.error("ギルド固有設定の保存中にエラーが発生しました。", e);
            }
        }
        command.getChannel().sendMessage("現在の音量は **" +
                Main.getController().getGuildSettingsTable().loadSettings(command.getGuild()).getVolume() + "%** です。").queue();
    }

    @Override
    public String help() {
        return "```%prefix%volume 現在のVolumeを表示します。\n" +
                "%prefix%volume <0-150> 音量を変更します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
