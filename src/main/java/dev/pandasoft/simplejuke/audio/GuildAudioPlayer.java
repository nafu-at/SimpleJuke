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
    private final SafeTrackManager trackManager;

    public GuildAudioPlayer(AudioPlayerManager manager, Guild guild) {
        playerManager = manager;
        this.guild = guild;
        link = Main.getController().getConfig().getAdvancedConfig().isUseNodeServer() ?
                Main.getController().getLavalink().getLink(guild) : null;
        player = link != null ? link.getPlayer() : new LavaplayerPlayerWrapper(playerManager.createPlayer());

        trackManager = new SafeTrackManager(guild, player);
        player.addListener(trackManager);
    }

    public Guild getGuild() {
        return guild;
    }

    /**
     * ノードと接続している場合はノードとの接続状況を取得します。
     *
     * @return ノードとの接続状況。接続されていない場合はnull
     */
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

    /**
     * 現在再生中のトラックを返します。
     * このクラスでは稼働中のPlayerとの整合性を確認します。
     *
     * @return 再生中のトラック。何も再生されていない場合や再生中のトラックとキューの整合性がとれない場合はnull
     */
    public AudioTrackContext getNowPlaying() {
        return trackManager.getNowPlaying();
    }

    /**
     * 再生中のトラックの再生箇所時間を返します。
     *
     * @return 再生中のトラックの再生箇所時間(ミリ秒)
     */
    public Long getTrackPosition() {
        return player.getTrackPosition();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return link == null ? new AudioPlayerSendHandler(player) : null;
    }

    /**
     * 指定したチャンネルにBotを接続させます。
     *
     * @param targetChannel Botを接続させるチャンネル
     */
    public void joinChannel(VoiceChannel targetChannel) {
        setVolume(Main.getController().getGuildSettingsManager().loadSettings(guild).getVolume());
        if (link != null) {
            link.connect(targetChannel);
        } else {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(targetChannel);
        }
    }

    /**
     * Botをチャンネルから切断させます。
     */
    public void leaveChannel() {
        if (link != null) {
            link.disconnect();
        } else {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.closeAudioConnection();
        }
    }

    /**
     * 再生中のトラックを停止してノードとの接続を切断します。
     */
    public void destroy() {
        if (link != null) {
            stop();
            player.removeListener(trackManager);
            link.destroy();
        }
    }

    /**
     * 現在登録済みのキューの一覧を返します。
     *
     * @return 現在登録済みのキューの一覧
     */
    public List<AudioTrackContext> getQueues() {
        return trackManager.getQueues();
    }

    public void play() {
        player.setPaused(false);
        trackManager.safeStart();
    }

    public void play(AudioTrackContext track) {
        trackManager.queue(track, 0);
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

    /**
     * プレイヤーの一時停止状況を切り替えます。
     */
    public void pause() {
        if (player.isPaused()) {
            player.setPaused(false);
            trackManager.safeStart();
        } else {
            player.setPaused(true);
        }
    }

    /**
     * プレイヤーの一時停止状況を変更します。
     *
     * @param pause 変更するプレイヤーの一時停止状況
     */
    public void pause(boolean pause) {
        player.setPaused(pause);
    }

    /**
     * キューをシャッフルします。
     */
    public void shuffle() {
        trackManager.shuffle();
    }

    /**
     * プレイヤーの再生状況を返します。
     *
     * @return トラックの再生中の場合はtrue
     */
    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    /**
     * プレイヤーの一時停止状況を返します。
     *
     * @return プレイヤーが一時停止している場合はtrue
     */
    public boolean isPaused() {
        return player.isPaused();
    }

    /**
     * 再生中の楽曲を指定したポジションにシークします。
     *
     * @param time シークするポジション(ミリ秒)
     */
    public void seek(Long time) {
        player.seekTo(time);
    }

    /**
     * 現在設定されているプレイヤーの音量を返します。
     *
     * @return 現在設定されているプレイヤーの音量
     */
    public int getVolume() {
        return player.getVolume();
    }

    /**
     * プレイヤーの音量を変更します。
     *
     * @param volume 変更するプレイヤー音量
     */
    public void setVolume(int volume) {
        player.setVolume(volume);
    }
}
