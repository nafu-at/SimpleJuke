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
import dev.pandasoft.simplejuke.discord.command.CommandTempRegistry;
import dev.pandasoft.simplejuke.http.youtube.SearchItem;
import dev.pandasoft.simplejuke.http.youtube.YouTubeAPIClient;
import dev.pandasoft.simplejuke.http.youtube.YouTubeSearchResults;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class PlayCommand extends CommandExecutor {
    private static final Pattern URL_REGEX = Pattern.compile("^(http|https)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
    private static final Pattern NOMBER_REGEX = Pattern.compile("^[1-5]*$");

    public PlayCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (!command.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            VoiceChannel targetChannel = command.getInvoker().getVoiceState().getChannel();
            if (targetChannel == null) {
                command.getChannel().sendMessage("ボイスチャンネルに接続してから実行して下さい。").queue();
                return;
            }
            audioPlayer.joinChannel(targetChannel);
        }

        if (command.getArgs().length == 0) {
            audioPlayer.pause(false);

            if (!command.getMessage().getAttachments().isEmpty())
                command.getMessage().getAttachments().forEach(attachment -> audioPlayer.loadItemOrdered(attachment.getUrl(),
                        command.getInvoker()));
        } else {
            if (URL_REGEX.matcher(command.getArgs()[0]).find()) {
                audioPlayer.pause(false);

                audioPlayer.loadItemOrdered(command.getArgs()[0], command.getInvoker());
                if (command.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE))
                    command.getMessage().delete().submit();
            } else if (NOMBER_REGEX.matcher(command.getArgs()[0]).find()) {
                Object object = CommandTempRegistry.removeTemp(command.getGuild());
                if (object instanceof YouTubeSearchResults) {
                    YouTubeSearchResults searchResult = (YouTubeSearchResults) object;
                    audioPlayer.loadItemOrdered("https://www.youtube.com/watch?v="
                                    + searchResult.getItems()[Integer.parseInt(command.getArgs()[0]) - 1].getID().getVideoID(),
                            command.getInvoker());
                    if (command.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE))
                        command.getMessage().delete().submit();
                }
            } else {
                File file = new File(command.getArgs()[0]);
                if (file.exists()) {
                    audioPlayer.loadItemOrdered(file.getPath(), command.getInvoker());
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (String arg : command.getArgs())
                    builder.append(arg + " ");
                try {
                    YouTubeSearchResults result = new YouTubeAPIClient().searchVideos(builder.toString());
                    if (result == null || result.getItems().length == 0) {
                        command.getChannel().sendMessage("条件に該当する項目が見つかりませんでした。").queue();
                        return;
                    }

                    StringBuilder message = new StringBuilder();
                    message.append("**以下の項目が見つかりました。**");
                    int count = 1;
                    for (SearchItem item : result.getItems()) {
                        message.append("\n`[" + count + "]` " + item.getSnippet().getTitle() + "");
                        count++;
                    }
                    message.append("\n\n**再生するには `play [1-5]` で選択してください。**");

                    CommandTempRegistry.registerTemp(command.getGuild(), result);
                    command.getChannel().sendMessage(message.toString()).queue();
                } catch (IOException e) {
                    ExceptionUtil.sendStackTrace(command.getGuild(), e, "検索結果の取得に失敗しました。");
                }
            }
        }
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
