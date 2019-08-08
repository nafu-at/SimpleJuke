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
import dev.pandasoft.simplejuke.audio.AudioTrackContext;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;

public class SkipCommand extends CommandExecutor {

    public SkipCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (audioPlayer.getNowPlaying() != null) {
            if (!command.getMessage().getMentionedMembers().isEmpty()) {
                int skipcount =
                        command.getMessage().getMentionedMembers().stream().mapToInt(member -> audioPlayer.skip(member).size()).sum();
                command.getChannel().sendMessage("**" + skipcount + "曲をスキップしました。**").queue();
            } else if (command.getArgs().length >= 1) {
                String indexS = command.getArgs()[0];
                if (indexS.contains("-")) {
                    String[] split = indexS.split("-");
                    if (split.length == 1 && indexS.endsWith("-")) {
                        int below = Integer.parseInt(indexS.replace("-", ""));
                        audioPlayer.skip(below);
                        command.getChannel().sendMessage("`#" + below + "` 以降のトラックをスキップしました。").queue();
                    } else {
                        audioPlayer.skip(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                        command.getChannel().sendMessage("`#" + split[0] + "` から " + "`#" + split[1] + "` をスキップしました。").queue();
                    }
                } else {
                    try {
                        command.getChannel().sendMessage("**"
                                + audioPlayer.skip(Integer.parseInt(indexS), Integer.parseInt(indexS)).get(0).getTrack().getInfo().title
                                + "** をスキップしました。").queue();
                    } catch (IllegalArgumentException e) {
                        // nothing
                    }
                }
            } else {
                AudioTrackContext nowPlaying = audioPlayer.getNowPlaying();
                audioPlayer.skip();
                command.getChannel().sendMessage("**" + nowPlaying.getTrack().getInfo().title + "** をスキップしました。").queue();
            }
        }
    }

    @Override
    public String help() {
        return "```%prefix%skip 現在再生中の楽曲をスキップします。\n" +
                "%prefix%skip [track] 指定した番号の楽曲をスキップします。\n" +
                "%prefix%skip [from-to] 指定した範囲の楽曲をスキップします。\n" +
                "%prefix%skip [below-] 指定した番号以降の楽曲をスキップします。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
