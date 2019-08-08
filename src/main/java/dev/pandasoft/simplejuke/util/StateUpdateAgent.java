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

package dev.pandasoft.simplejuke.util;

import dev.pandasoft.simplejuke.Main;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.bot.sharding.ShardManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StateUpdateAgent implements Runnable {
    private int guildCount;
    private int userCount;
    private int textChannelCount;
    private int voiceChannelCount;

    private Date latestUpdate;

    @Override
    public void run() {
        while (true) {
            latestUpdate = new Date();

            ShardManager shardManager = Main.getController().getShardManager();
            guildCount = shardManager.getGuilds().size();
            userCount = shardManager.getUsers().size();
            textChannelCount = shardManager.getTextChannels().size();
            voiceChannelCount = shardManager.getVoiceChannels().size();

            Main.getController().getInfoReader().loadUpdateInfo();

            try {
                Thread.sleep(TimeUnit.HOURS.toMillis(1));
            } catch (InterruptedException e) {
                log.error("統計情報更新の待機中に問題が発生しました。", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getGuildCount() {
        return guildCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public int getTextChannelCount() {
        return textChannelCount;
    }

    public int getVoiceChannelCount() {
        return voiceChannelCount;
    }

    public Date getLatestUpdate() {
        return latestUpdate;
    }
}
