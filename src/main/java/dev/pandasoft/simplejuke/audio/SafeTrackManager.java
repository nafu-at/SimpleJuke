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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.event.PlayerEventListenerAdapter;
import dev.pandasoft.simplejuke.database.legacy.entities.RepeatSetting;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lavalink.client.player.IPlayer;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SafeTrackManager extends PlayerEventListenerAdapter {
    private final Guild guild;
    private final IPlayer player;
    private final BlockingQueue<AudioTrackContext> queue = new LinkedBlockingQueue<>();
    private Date lastPlayed;

    public SafeTrackManager(Guild guild, IPlayer player) {
        this.guild = guild;
        this.player = player;
    }

    void queue(AudioTrackContext context, int desiredNum) {
        List<AudioTrackContext> tracks = getQueues();

        if (desiredNum == 0 || desiredNum > tracks.size() || (desiredNum == 1 && tracks.isEmpty())) {
            if (player.getPlayingTrack() == null)
                player.playTrack(context.getTrack());
            queue.offer(context);
            if (Main.getController().getGuildSettingsTable().loadSettings(guild).isShuffle())
                shuffle();
        } else if (desiredNum == 1 && tracks.size() == 1) {
            queue.offer(context);
            skip();
        } else {
            List<AudioTrackContext> before;
            List<AudioTrackContext> after;
            if (desiredNum == 1) {
                before = tracks.subList(0, 1);
                after = tracks.subList(1, tracks.size());
            } else {
                before = tracks.subList(0, desiredNum - 1);
                after = tracks.subList(desiredNum - 1, tracks.size());
            }

            queue.clear();
            before.forEach(queue::add);
            queue.add(context);
            after.forEach(queue::add);

            if (desiredNum == 1)
                skip();
        }
    }

    synchronized void stop() {
        queue.clear();
        player.stopTrack();
    }

    synchronized void skip() {
        if (Main.getController().getGuildSettingsTable().loadSettings(guild).getRepeat() == RepeatSetting.SINGLE)
            queue.poll();
        player.stopTrack();
    }

    synchronized List<AudioTrackContext> skip(int below) {
        return skip(below, queue.size());
    }

    synchronized List<AudioTrackContext> skip(int from, int to) {
        List<AudioTrackContext> tracks = getQueues();
        List<AudioTrackContext> toDelete = new ArrayList<>();
        for (int index = from - 1; index < to; index++) {
            toDelete.add(tracks.get(index));
        }
        toDelete.forEach(queue::remove);
        if (from == 1)
            skip();
        return toDelete;
    }

    synchronized List<AudioTrackContext> skip(Member invoker) {
        List<AudioTrackContext> tracks = getQueues();
        List<AudioTrackContext> toDelete = new ArrayList<>();
        tracks.forEach(track -> {
            if (track.getInvoker().equals(invoker))
                toDelete.add(track);
        });
        Boolean doSkip = toDelete.contains(getNowPlaying());
        toDelete.forEach(queue::remove);
        if (doSkip)
            skip();

        return toDelete;
    }

    synchronized void shuffle() {
        if (queue.isEmpty())
            return;

        AudioTrackContext nowPlaying = getNowPlaying();
        List<AudioTrackContext> queues = getQueues();
        if (nowPlaying != null)
            queues.remove(nowPlaying);

        Collections.shuffle(queues);
        queue.clear();
        if (nowPlaying == null) {
            queues.forEach(queue::add);
        } else {
            queue.add(nowPlaying);
            queues.forEach(queue::add);
        }
    }


    /**
     * 現在再生中のトラックを返します。
     * このクラスでは稼働中のPlayerとの整合性を確認します。
     *
     * @return 再生中のトラック。何も再生されていない場合や再生中のトラックとキューの整合性がとれない場合はnull
     */
    synchronized AudioTrackContext getNowPlaying() {
        AudioTrackContext nowPlaying = queue.peek();

        if (nowPlaying != null) {
            AudioTrack track = player.getPlayingTrack();
            if (track != null) {
                if (nowPlaying.getTrack().getIdentifier().equals(track.getIdentifier())) {
                    return nowPlaying;
                } else { // 再生中のトラックと記録されているキューが一致しない場合再生中のトラックをキューの中から探す
                    List<AudioTrackContext> queues = getQueues();
                    for (int nom = 0; queues.size() > nom; nom++) {
                        if (queues.get(nom).getTrack().equals(track)) {
                            // 見つかった場合はその前までのトラックを削除
                            while (nom > 0) {
                                //削除中にキューが空になった場合はnullを返す。
                                if (queue.poll() != null)
                                    return nowPlaying;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 登録されているすべてのキューを返します。
     *
     * @return 登録されているすべてのキューを格納したList
     */
    synchronized List<AudioTrackContext> getQueues() {
        return new ArrayList<>(queue);
    }

    synchronized void safeStart() {
        if (player.getPlayingTrack() == null) {
            List<AudioTrackContext> tracks = getQueues();
            if (!queue.isEmpty()) {
                AudioTrackContext latestTrack = queue.poll();
                queue.clear();
                queue.add(latestTrack.makeClone());
                tracks.forEach(queue::add);
                nextTrack();
            }
        }
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    synchronized void nextTrack() {
        AudioTrackContext track = queue.peek();
        if (track != null) {
            player.playTrack(track.getTrack());
            lastPlayed = new Date();
        }
    }

    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        AudioTrackContext latestTrack = queue.peek();

        if (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.STOPPED) {
            if (latestTrack != null &&
                    Main.getController().getGuildSettingsTable().loadSettings(guild).getRepeat() == RepeatSetting.SINGLE &&
                    track.getIdentifier().equals(latestTrack.getTrack().getIdentifier())) {
                queue.poll();

                List<AudioTrackContext> tracks = new ArrayList<>(queue);
                queue.clear();
                queue.add(latestTrack.makeClone());
                tracks.forEach(queue::add);
            } else if (latestTrack != null && Main.getController().getGuildSettingsTable().loadSettings(guild).getRepeat() == RepeatSetting.ALL) {
                queue.poll();
                queue.offer(latestTrack.makeClone());
            } else if (latestTrack != null && track.getIdentifier().equals(latestTrack.getTrack().getIdentifier())) {
                queue.poll();
            }

            nextTrack();
        }
    }

    @Override
    public void onTrackStart(IPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        if (Main.getController().getGuildSettingsTable().loadSettings(guild).isAnnounce())
            MessageUtil.sendMessage(guild, "**" + track.getInfo().title + "**の再生を開始します。");
    }

    @Override
    public void onPlayerPause(IPlayer player) {
        super.onPlayerPause(player);
        MessageUtil.sendMessage(guild, "再生を一時停止しました。再開するにはもう一度入力してください。");
    }

    @Override
    public void onPlayerResume(IPlayer player) {
        super.onPlayerResume(player);
        MessageUtil.sendMessage(guild, "再生を再開します。");
        if (getNowPlaying() == null)
            nextTrack();
    }

    @Override
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        super.onTrackException(player, track, exception);
        ExceptionUtil.sendStackTrace(guild, exception);
    }
}
