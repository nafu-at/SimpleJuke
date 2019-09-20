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

package dev.pandasoft.simplejuke.discord.command.executor.info;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.StateUpdateAgent;
import dev.pandasoft.simplejuke.util.UpdateInfoReader;

import java.lang.management.ManagementFactory;

public class StatusCommand extends CommandExecutor {

    public StatusCommand(String name, String... aliases) {
        super(name, aliases);
    }

    private static String formatTime(long millis) {
        long t = millis / 1000L;
        int sec = (int) (t % 60L);
        int min = (int) ((t % 3600L) / 60L);
        int hrs = (int) (t / 3600L);

        String timestamp;

        if (hrs != 0)
            timestamp = hrs + "時間 " + min + "分" + sec + "秒";
        else
            timestamp = min + "分" + sec + "秒";
        return timestamp;
    }

    @Override
    public void onInvoke(BotCommand command) {
        long max = Runtime.getRuntime().maxMemory() / 1048576L;
        long total = Runtime.getRuntime().totalMemory() / 1048576L;
        long free = Runtime.getRuntime().freeMemory() / 1048576L;
        long useing = total - free;
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        StateUpdateAgent agent = Main.getController().getUpdateAgent();

        StringBuilder builder = new StringBuilder();
        builder.append("このBotは起動されてから " + formatTime(uptime) + "間実行されています。\n");
        builder.append("```\n");
        builder.append("====== Memory Info ======\n");
        builder.append("予約済みメモリ:       " + total + "MB\n");
        builder.append("  -> 使用中:          " + useing + "MB\n");
        builder.append("  -> 空き:            " + free + "MB\n");
        builder.append("最大予約可能メモリ:   " + max + "MB\n\n");
        builder.append("====== Statistic Info ======\n");
        builder.append("導入済サーバー数:     " + agent.getGuildCount() + "\n");
        builder.append("参加ユーザー数:       " + agent.getUserCount() + "\n");
        builder.append("テキストチャンネル数: " + agent.getTextChannelCount() + "\n");
        builder.append("ボイスチャンネル数:   " + agent.getVoiceChannelCount() + "\n\n");
        builder.append("====== Version Info ======\n");
        builder.append("SimpleJuke:           " + UpdateInfoReader.getNowVersion() + "\n");
        builder.append("LavaPlayer:           " + PlayerLibrary.VERSION + "\n\n");
        builder.append("====== Agent Info ======\n");
        builder.append("StateUpdateAgent: " + agent.getLatestUpdate().toString() + "\n");
        builder.append("```");

        command.getChannel().sendMessage(builder.toString()).queue();
    }

    @Override
    public String help() {
        return "%prefix%status Botに関する情報を表示します。";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }
}
