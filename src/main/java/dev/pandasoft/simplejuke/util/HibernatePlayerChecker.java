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

import java.util.Calendar;
import java.util.Date;

public class HibernatePlayerChecker implements Runnable {
    private Date latestCheck;

    @Override
    public void run() {
        latestCheck = new Date();

        Main.getController().getPlayerRegistry().getPlayers().forEach(guildAudioPlayer -> {
            if (!guildAudioPlayer.isPlaying()) {
                Calendar threshold = Calendar.getInstance();
                threshold.setTime(guildAudioPlayer.getLastPlayed());
                threshold.add(Calendar.MINUTE, 30);
                if (latestCheck.after(threshold.getTime())) {
                    Main.getController().getPlayerRegistry().destroyPlayer(guildAudioPlayer.getGuild());
                    MessageUtil.sendMessage(guildAudioPlayer.getGuild(), "長時間使われていないようですね？\n" +
                            "リソース削減のためプレイヤーは自動的に退出します。");
                }
            }
        });
    }

    public Date getLatestCheck() {
        return latestCheck;
    }
}
