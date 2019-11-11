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

/*
 * This source code is a modification of part of the source code licensed by Frederik Ar. Mikkelsen & NoobLance.
 * The original source code license is as follows.
 */

package dev.pandasoft.simplejuke.audio.event;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import lavalink.client.player.event.*;

public class PlayerEventListenerAdapter implements IPlayerEventListener, AudioEventListener {

    /**
     * @param player Audio player
     */
    public void onPlayerPause(IPlayer player) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     */
    public void onPlayerResume(IPlayer player) {
        // Adapter dummy method
    }

    /**
     * @param player Audio player
     * @param track  Audio track that started
     */
    public void onTrackStart(IPlayer player, AudioTrack track) {
        // Adapter dummy method
    }

    /**
     * @param player    Audio player
     * @param track     Audio track that ended
     * @param endReason The reason why the track stopped playing
     */
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Adapter dummy method
    }

    /**
     * @param player    Audio player
     * @param track     Audio track where the exception occurred
     * @param exception The exception that occurred
     */
    public void onTrackException(IPlayer player, AudioTrack track, Exception exception) {
        // Adapter dummy method
    }

    /**
     * @param player      Audio player
     * @param track       Audio track where the exception occurred
     * @param thresholdMs The wait threshold that was exceeded for this event to trigger
     */
    public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
        // Adapter dummy method
    }


    @Override
    public void onEvent(PlayerEvent event) {
        if (event instanceof PlayerPauseEvent) {
            onPlayerPause(event.getPlayer());
        } else if (event instanceof PlayerResumeEvent) {
            onPlayerResume(event.getPlayer());
        } else if (event instanceof TrackStartEvent) {
            onTrackStart(event.getPlayer(), ((TrackStartEvent) event).getTrack());
        } else if (event instanceof TrackEndEvent) {
            onTrackEnd(event.getPlayer(), ((TrackEndEvent) event).getTrack(), ((TrackEndEvent) event).getReason());
        } else if (event instanceof TrackExceptionEvent) {
            onTrackException(event.getPlayer(), ((TrackExceptionEvent) event).getTrack(), ((TrackExceptionEvent) event).getException());
        } else if (event instanceof TrackStuckEvent) {
            onTrackStuck(event.getPlayer(), ((TrackStuckEvent) event).getTrack(), ((TrackStuckEvent) event).getThresholdMs());
        }
    }

    /**
     * This code may cause problems in future specification changes.
     */
    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.PlayerPauseEvent) {
            onPlayerPause(new LavaplayerPlayerWrapper(event.player));
        } else if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.PlayerResumeEvent) {
            onPlayerResume(new LavaplayerPlayerWrapper(event.player));
        } else if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent) {
            onTrackStart(new LavaplayerPlayerWrapper(event.player),
                    ((com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent) event).track);
        } else if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) {
            onTrackEnd(new LavaplayerPlayerWrapper(event.player),
                    ((com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) event).track, ((com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent) event).endReason);
        } else if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) {
            onTrackException(new LavaplayerPlayerWrapper(event.player),
                    ((com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) event).track, ((com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent) event).exception);
        } else if (event instanceof com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent) {
            onTrackStuck(new LavaplayerPlayerWrapper(event.player),
                    ((com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent) event).track, ((com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent) event).thresholdMs);
        }
    }
}
