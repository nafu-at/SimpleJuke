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

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import lavalink.client.io.LavalinkSocket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NodesCommand extends CommandExecutor {

    public NodesCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (Main.getController().getConfig().getAdvancedConfig().isUseNodeServer()) {
            if (command.getArgs().length == 0) {
                StringBuilder builder = new StringBuilder();
                List<LavalinkSocket> nodes = Main.getController().getLavalink().getNodes();
                builder.append("このBotには" + nodes.size() + "個のノードが登録されています。\n");
                builder.append("```");
                nodes.forEach(node -> {
                    builder.append("\nNodeName: " + node.getName() + "\n");
                    builder.append("Address: " + node.getRemoteUri() + "\n");
                    builder.append("Memory: " + fromByteToMB(node.getStats().getMemUsed()) + "MB / " + fromByteToMB(node.getStats().getMemReservable()) + "MB\n");
                    builder.append("Players: " + node.getStats().getPlayingPlayers() + " / " + node.getStats().getPlayers() + "\n");
                });
                builder.append("```");
                command.getChannel().sendMessage(builder.toString()).queue();
            } // TODO: 2019-08-04 ノード名指定で情報を表示する機能の実装
        } else {
            command.getChannel().sendMessage("このBotはノードが有効化されていません！").queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%nodes Botに接続されているLavaLinkノード一覧を表示します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }

    private float fromByteToMB(long value) {
        BigDecimal bd = new BigDecimal((float) value / 1024 / 1014);
        return bd.setScale(1, RoundingMode.HALF_UP).floatValue();
    }
}
