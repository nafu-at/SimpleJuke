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

package dev.pandasoft.simplejuke.discord.command.executor.music.info;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.AudioTrackContext;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.MessageUtil;

public class TimeCommand extends CommandExecutor {

    public TimeCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (audioPlayer.isPlaying()) {
            AudioTrackContext trackContext = audioPlayer.getNowPlaying();
            if (trackContext != null) {
                AudioTrack audioTrack = trackContext.getTrack();
                command.getChannel().sendMessage("\\▶ **" + audioTrack.getInfo().title + "**を再生中です。\n" +
                        "`" + MessageUtil.formatTime(audioTrack.getDuration()) + "` のうち `" +
                        MessageUtil.formatTime(audioPlayer.getTrackPosition()) + "` を再生しています。\n" +
                        "残り時間は `" + MessageUtil.formatTime(audioTrack.getDuration() - audioPlayer.getTrackPosition()) +
                        "` です。").queue();
            }
        } else {
            command.getChannel().sendMessage("今は何も再生されていません!").queue();
        }
    }

    @Override
    public String help() {
        return "%prefix%time 現在再生中のトラックの再生時間を表示します。";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
