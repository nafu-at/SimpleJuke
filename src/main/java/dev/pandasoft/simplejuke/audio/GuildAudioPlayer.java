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

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.pandasoft.simplejuke.Main;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class GuildAudioPlayer {
    private final AudioPlayerManager playerManager;
    private final Guild guild;
    private final JdaLink link;
    private final IPlayer player;
    private final TrackManager trackManager;

    public GuildAudioPlayer(AudioPlayerManager manager, Guild guild) {
        playerManager = manager;
        this.guild = guild;
        link = Main.getController().getConfig().getAdvancedConfig().isUseNodeServer() ?
                Main.getController().getLavalink().getLink(guild) : null;
        player = link != null ? link.getPlayer() : new LavaplayerPlayerWrapper(playerManager.createPlayer());

        trackManager = new TrackManager(guild, player);
        player.addListener(trackManager);
    }

    public Guild getGuild() {
        return guild;
    }

    public Link.State getLinkStatus() {
        if (link != null)
            return link.getState();
        return null;
    }

    public synchronized void loadItemOrdered(String truckUrl, Member invoker) {
        loadItemOrdered(truckUrl, invoker, 0);
    }

    public synchronized void loadItemOrdered(String truckUrl, Member invoker, int desiredNum) {
        playerManager.loadItemOrdered(this, truckUrl, new AudioLoader(this, invoker, desiredNum));
    }

    public AudioTrackContext getNowPlaying() {
        return trackManager.getNowPlaying();
    }

    public Long getTrackPosition() {
        return player.getTrackPosition();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return link == null ? new AudioPlayerSendHandler(player) : null;
    }

    public void joinChannel(VoiceChannel targetChannel) {
        setVolume(Main.getController().getGuildSettingsManager().loadSettings(guild).getVolume());
        if (link != null) {
            link.connect(targetChannel);
        } else {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(targetChannel);
        }
    }

    public void leaveChannel() {
        if (link != null) {
            link.disconnect();
        } else {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.closeAudioConnection();
        }
    }

    public void destroy() {
        if (link != null) {
            stop();
            player.removeListener(trackManager);
            link.destroy();
        }
    }

    public List<AudioTrackContext> getQueues() {
        return trackManager.getQueues();
    }

    public void play(AudioTrackContext track) {
        trackManager.queue(track);
    }

    public void play(AudioTrackContext track, int desiredNum) {
        trackManager.queue(track, desiredNum);
    }

    public void stop() {
        trackManager.stop();
    }

    public void skip() {
        trackManager.skip();
    }

    public List<AudioTrackContext> skip(int below) {
        return trackManager.skip(below);
    }

    public List<AudioTrackContext> skip(int from, int to) {
        return trackManager.skip(from, to);
    }

    public List<AudioTrackContext> skip(Member member) {
        return trackManager.skip(member);
    }

    public void pause() {
        player.setPaused(!player.isPaused());
        if (!player.isPaused() && player.getPlayingTrack() == null)
            trackManager.skip();
    }

    public void pause(boolean pause) {
        player.setPaused(pause);
    }

    public void shuffle() {
        trackManager.shuffle();
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public void seek(Long time) {
        player.seekTo(time);
    }

    public int getVolume() {
        return player.getVolume();
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }
}
