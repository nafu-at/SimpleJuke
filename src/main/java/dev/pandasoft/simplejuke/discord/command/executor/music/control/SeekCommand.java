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
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.MessageUtil;

public class SeekCommand extends CommandExecutor {

    public SeekCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (command.getArgs().length != 0) {
            GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
            if (audioPlayer.getNowPlaying() != null)
                audioPlayer.seek(MessageUtil.parseTimeToMillis(command.getArgs()[0]));
        }
    }

    @Override
    public String help() {
        return "```%prefix%seek [position] 指定したポジションにシークします。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
