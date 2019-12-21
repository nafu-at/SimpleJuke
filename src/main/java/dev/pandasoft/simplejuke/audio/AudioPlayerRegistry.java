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
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.pandasoft.simplejuke.util.MessageUtil;
import lavalink.client.io.Link;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayerRegistry {
    private final Map<Guild, GuildAudioPlayer> players = new HashMap<>();
    private final AudioPlayerManager playerManager;

    public AudioPlayerRegistry(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildAudioPlayer getGuildAudioPlayer(Guild guild) {
        GuildAudioPlayer player = players.computeIfAbsent(guild,
                key -> new GuildAudioPlayer(playerManager, guild));
        if (player.getSendHandler() != null)
            guild.getAudioManager().setSendingHandler(player.getSendHandler());
        return player;
    }

    public synchronized void destroyPlayer(Guild guild) {
        GuildAudioPlayer player = players.get(guild);
        if (player != null) {
            if (player.getLinkStatus() == null) {
                player.stop();
                player.leaveChannel();
            } else if (player.getLinkStatus() == Link.State.DESTROYED) {
                MessageUtil.sendMessage(guild, "プレイヤーは既に破棄されています。この問題は通常発生しません。\n" +
                        "プレイヤーは何もせず削除されます。");
            } else {
                player.destroy();
            }
            players.remove(guild);
        }
    }

    public synchronized List<GuildAudioPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }
}
