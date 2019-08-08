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

package dev.pandasoft.simplejuke.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.config.MusicSourceSection;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AudioLoader implements AudioLoadResultHandler {
    private GuildAudioPlayer audioPlayer;
    private Member invoker;
    private int desiredNum;

    public AudioLoader(GuildAudioPlayer audioPlayer, Member invoker, int desiredNum) {
        this.audioPlayer = audioPlayer;
        this.invoker = invoker;
        this.desiredNum = desiredNum;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (!checkAudioSorce(track))
            return;

        if (desiredNum == 0)
            audioPlayer.play(new AudioTrackContext(invoker.getGuild(), invoker, track));
        else
            audioPlayer.play(new AudioTrackContext(invoker.getGuild(), invoker, track), desiredNum);

        List<AudioTrackContext> tracks = audioPlayer.getQueues();
        log.debug("Track Queued. Total: {}, TracksInfo: {}", tracks.size(), tracks.toString());
        if (tracks.size() == 1)
            MessageUtil.sendMessage(audioPlayer.getGuild(), "**" + track.getInfo().title + "**を再生します。");
        else
            MessageUtil.sendMessage(audioPlayer.getGuild(), "**" + track.getInfo().title + "**をキューに追加しました。");
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();
        List<AudioTrack> tracks = new ArrayList<>(playlist.getTracks());
        if (firstTrack == null) {
            if (Main.getController().getGuildSettingsManager().loadSettings(invoker.getGuild()).isShuffle())
                Collections.shuffle(tracks);
            firstTrack = tracks.get(0);
            if (!checkAudioSorce(firstTrack))
                return;
        }
        tracks.remove(firstTrack);
        audioPlayer.play(new AudioTrackContext(invoker.getGuild(), invoker, firstTrack));
        tracks.forEach(track -> audioPlayer.play(new AudioTrackContext(invoker.getGuild(), invoker, track)));
        MessageUtil.sendMessage(audioPlayer.getGuild(), "**" + playlist.getName() + "** から `" + playlist.getTracks().size() + "`曲をロードしました。");
    }

    @Override
    public void noMatches() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        ExceptionUtil.sendStackTrace(audioPlayer.getGuild(), exception, "トラックの読み込みに失敗しました。");
    }

    private boolean checkAudioSorce(AudioTrack track) {
        MusicSourceSection musicSource = Main.getController().getConfig().getBasicConfig().getMusicSource();

        if (track instanceof YoutubeAudioTrack && !musicSource.enableYoutube()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "YouTubeからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof SoundCloudAudioTrack && !musicSource.enableSoundCloud()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "SoundCloudからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof BandcampAudioTrack && !musicSource.enableBandCamp()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "BandCampからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof VimeoAudioTrack && !musicSource.enableVimeo()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "Vimeoからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof TwitchStreamAudioTrack && !musicSource.enableTwitch()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "Twitchからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof HttpAudioTrack && !musicSource.enableHttp()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "HTTPからの再生は設定によって無効化されています。");
            return false;
        } else if (track instanceof LocalAudioTrack && !musicSource.enableLocal()) {
            MessageUtil.sendMessage(audioPlayer.getGuild(), "ローカルファイルからの再生は設定によって無効化されています。");
            return false;
        }
        return true;
    }
}
