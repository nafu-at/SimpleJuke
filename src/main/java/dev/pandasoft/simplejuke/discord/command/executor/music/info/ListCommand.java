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

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.AudioTrackContext;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ListCommand extends CommandExecutor {

    public ListCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        List<AudioTrackContext> tracks = audioPlayer.getQueues();
        if (!tracks.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int range = Main.getController().getGuildSettingsTable().loadSettings(command.getGuild()).getListRange();
            int page = 1;

            if (command.getArgs().length != 0) {
                try {
                    page = Integer.parseInt(command.getArgs()[0]);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    command.getChannel().sendMessage("ページ数は数字で指定してください。").queue();
                }
            }

            int listPage = tracks.size() / range;
            if (tracks.size() % range >= 1)
                listPage++;

            if (page > listPage) {
                command.getChannel().sendMessage("指定されたページはありません！").queue();
                return;
            }

            long totalTime = 0;
            for (AudioTrackContext context : tracks)
                totalTime += context.getTrack().getDuration();

            sb.append("**現在" + tracks.size() + "曲がキューに追加されています。** `[" + page + "/" + listPage + "]`" +
                    " `(" + MessageUtil.formatTime(totalTime) + ")`");
            for (int count = range * page - range + 1; count <= range * page; count++) {
                if (tracks.size() >= count) {
                    AudioTrackContext track = tracks.get(count - 1);
                    if (count == 1)
                        sb.append("\n`[" + count + "]` \\▶ **" + track.getTrack().getInfo().title +
                                " (" + track.getInvoker().getEffectiveName() + ")** `[" + MessageUtil.formatTime(track.getTrack().getDuration()) + "]`");
                    else
                        sb.append("\n`[" + count + "]` **" + track.getTrack().getInfo().title
                                + " (" + track.getInvoker().getEffectiveName() + ")** `[" + MessageUtil.formatTime(track.getTrack().getDuration()) + "]`");
                }
            }
            command.getChannel().sendMessage(sb.toString()).queue();
        } else {
            command.getChannel().sendMessage("何もキューに入っていないようです。なにか新しい曲を聞いてみませんか？").queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%list 登録されているキューを一覧で表示します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
