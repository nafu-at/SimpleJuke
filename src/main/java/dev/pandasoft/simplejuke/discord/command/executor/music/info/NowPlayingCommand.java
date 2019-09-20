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

import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.AudioTrackContext;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.http.youtube.YouTubeAPIClient;
import dev.pandasoft.simplejuke.http.youtube.YouTubeObjectItem;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import dev.pandasoft.simplejuke.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;

public class NowPlayingCommand extends CommandExecutor {
    private static final YouTubeAPIClient client = new YouTubeAPIClient();
    private static final Color YOUTUBE = new Color(255, 0, 0);
    private static final Color SOUNDCLOUD_COLOR = new Color(255, 85, 0);
    private static final Color BANDCAMP_COLOR = new Color(0, 161, 198);
    private static final Color TWITCH_COLOR = new Color(75, 54, 124);
    private static final Color VIMEO_COLOR = new Color(15, 174, 241);
    private static final Color BLACK = new Color(0, 0, 0);

    public NowPlayingCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (audioPlayer.isPlaying()) {
            AudioTrackContext trackContext = audioPlayer.getNowPlaying();
            if (trackContext != null) {
                AudioTrack audioTrack = trackContext.getTrack();
                if (audioTrack instanceof YoutubeAudioTrack) {
                    try {
                        command.getChannel().sendMessage(getYouTubeEmbed(audioPlayer)).queue();
                    } catch (IOException e) {
                        ExceptionUtil.sendStackTrace(command.getGuild(), e, "動画情報の取得に失敗しました。");
                    }
                } else if (audioTrack instanceof SoundCloudAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, SOUNDCLOUD_COLOR)).queue();
                } else if (audioTrack instanceof BandcampAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, BANDCAMP_COLOR)).queue();
                } else if (audioTrack instanceof VimeoAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, VIMEO_COLOR)).queue();
                } else if (audioTrack instanceof TwitchStreamAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, TWITCH_COLOR)).queue();
                } else if (audioTrack instanceof HttpAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, BLACK)).queue();
                } else if (audioTrack instanceof LocalAudioTrack) {
                    command.getChannel().sendMessage(getDefaultEmbed(audioPlayer, BLACK)).queue();
                }
            }
        } else {
            command.getChannel().sendMessage("今は何も再生されていません!").queue();
        }
    }

    private MessageEmbed getYouTubeEmbed(GuildAudioPlayer audioPlayer) throws IOException {
        if (StringUtils.isBlank(Main.getController().getConfig().getAdvancedConfig().getGoogleAPIToken()))
            return getDefaultEmbed(audioPlayer, YOUTUBE);

        YouTubeObjectItem youtubeVideo = client.getYoutubeObjects(YouTubeAPIClient.YOUTUBE_VIDEO,
                audioPlayer.getNowPlaying().getTrack().getIdentifier()).getItems()[0];
        YouTubeObjectItem youtubeChannel = client.getYoutubeObjects(YouTubeAPIClient.YOUTUBE_CHANNEL,
                youtubeVideo.getSnippet().getChannelID()).getItems()[0];

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(youtubeVideo.getSnippet().getLocalized().getTitle(),
                "https://www.youtube.com/watch?v=" + youtubeVideo.getID());
        builder.setColor(YOUTUBE);
        builder.setAuthor(youtubeChannel.getSnippet().getLocalized().getTitle(),
                "https://www.youtube.com/channel/" + youtubeVideo.getSnippet().getChannelID(),
                youtubeChannel.getSnippet().getThumbnails().getHigh().getURL());
        builder.setThumbnail(youtubeVideo.getSnippet().getThumbnails().getHigh().getURL());
        MessageEmbed.Field time = new MessageEmbed.Field("Time",
                "[" + MessageUtil.formatTime(audioPlayer.getTrackPosition()) + "/" + MessageUtil.formatTime(audioPlayer.getNowPlaying().getTrack().getDuration()) + "]",
                true);
        builder.addField(time);

        String descMessage = youtubeVideo.getSnippet().getLocalized().getDescription();
        if (descMessage.length() > 800)
            descMessage = descMessage.substring(0, 800) + " [...]";
        MessageEmbed.Field description = new MessageEmbed.Field("Description", descMessage, false);
        builder.addField(description);
        builder.setFooter(audioPlayer.getNowPlaying().getInvoker().getEffectiveName() + " によってリクエストされました。",
                audioPlayer.getNowPlaying().getInvoker().getUser().getAvatarUrl());
        return builder.build();
    }

    private MessageEmbed getDefaultEmbed(GuildAudioPlayer audioPlayer, Color color) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(audioPlayer.getNowPlaying().getTrack().getInfo().title);
        builder.setColor(color);
        builder.setAuthor(audioPlayer.getNowPlaying().getTrack().getInfo().author);
        MessageEmbed.Field time = new MessageEmbed.Field("Time",
                "[" + MessageUtil.formatTime(audioPlayer.getTrackPosition()) + "/" + MessageUtil.formatTime(audioPlayer.getNowPlaying().getTrack().getDuration()) + "]",
                true);
        builder.addField(time);
        MessageEmbed.Field source = new MessageEmbed.Field("",
                audioPlayer.getNowPlaying().getTrack().getSourceManager().getSourceName() + "からロードされました。", false);
        builder.addField(source);
        builder.setFooter(audioPlayer.getNowPlaying().getInvoker().getEffectiveName() + " によってリクエストされました。",
                audioPlayer.getNowPlaying().getInvoker().getUser().getAvatarUrl());
        return builder.build();
    }

    @Override
    public String help() {
        return "```%prefix%nowplaying 現在再生中の楽曲の情報を表示します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
