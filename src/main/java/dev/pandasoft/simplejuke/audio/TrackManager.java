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
import dev.pandasoft.simplejuke.database.entities.RepeatSetting;
import dev.pandasoft.simplejuke.util.ExceptionUtil;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends PlayerEventListenerAdapter {
    private final Guild guild;
    private final IPlayer player;
    private final BlockingQueue<AudioTrackContext> queue = new LinkedBlockingQueue<>();

    TrackManager(Guild guild, IPlayer player) {
        this.guild = guild;
        this.player = player;
    }

    void queue(AudioTrackContext context) {
        if (player.getPlayingTrack() == null)
            player.playTrack(context.getTrack());
        queue.offer(context);
        if (Main.getController().getGuildSettingsManager().loadSettings(guild).isShuffle())
            shuffle();
    }

    void queue(AudioTrackContext context, int desiredNum) {
        List<AudioTrackContext> tracks = new ArrayList<>(queue);

        if (desiredNum == 1 && tracks.size() == 1) {
            queue(context);
            skip();
        } else if (desiredNum == 0 || desiredNum > tracks.size() || (desiredNum == 1 && tracks.isEmpty())) {
            queue(context);
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

    synchronized void skip() {
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
            player.stopTrack();
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
            player.stopTrack();

        return toDelete;
    }

    synchronized AudioTrackContext getNowPlaying() {
        return queue.peek();
    }

    synchronized void stop() {
        queue.clear();
        player.stopTrack();
    }

    synchronized void shuffle() {
        if (queue.isEmpty())
            return;
        AudioTrackContext nowPlaying = queue.poll();
        List<AudioTrackContext> queues = getQueues();

        Collections.shuffle(queues);
        queue.clear();
        queue.add(nowPlaying);
        queues.forEach(queue::add);
    }

    synchronized List<AudioTrackContext> getQueues() {
        return new ArrayList<>(queue);
    }


    private void nextTrack() {
        AudioTrackContext track = queue.peek();
        if (track != null)
            player.playTrack(track.getTrack());
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
    }

    @Override
    public void onTrackStart(IPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        if (Main.getController().getGuildSettingsManager().loadSettings(guild).isAnnounce())
            MessageUtil.sendMessage(guild, "**" + track.getInfo().title + "**の再生を開始します。");
    }

    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        AudioTrackContext latestTrack = queue.poll();

        if (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.STOPPED) {
            if (latestTrack != null && Main.getController().getGuildSettingsManager().loadSettings(guild).getRepeat() == RepeatSetting.SINGLE) {
                List<AudioTrackContext> tracks = new ArrayList<>(queue);
                queue.clear();
                queue.add(latestTrack.makeClone());
                tracks.forEach(queue::add);
            } else if (latestTrack != null && Main.getController().getGuildSettingsManager().loadSettings(guild).getRepeat() == RepeatSetting.ALL) {
                queue.offer(latestTrack.makeClone());
            }
            nextTrack();
        }
    }

    @Override
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        super.onTrackException(player, track, exception);
        ExceptionUtil.sendStackTrace(guild, exception);
    }
}
