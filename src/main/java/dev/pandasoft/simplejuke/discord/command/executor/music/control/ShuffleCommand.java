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
import dev.pandasoft.simplejuke.database.entities.GuildSettings;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class ShuffleCommand extends CommandExecutor {

    public ShuffleCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        try {
            GuildSettings settings = Main.getController().getGuildSettingsManager().loadSettings(command.getGuild());
            GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
            if (settings.isShuffle()) {
                settings.setShuffle(false);
                command.getChannel().sendMessage("シャッフルを解除しました。").queue();
            } else {
                settings.setShuffle(true);
                command.getChannel().sendMessage("キューをシャッフルします。").queue();
                audioPlayer.shuffle();
            }
            Main.getController().getGuildSettingsManager().saveSettings(command.getGuild(), settings);
        } catch (SQLException | IOException e) {
            log.error("ギルド固有設定の保存中にエラーが発生しました。", e);
        }
    }

    @Override
    public String help() {
        return "```%prefix%shuffle キューをシャッフルします。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
