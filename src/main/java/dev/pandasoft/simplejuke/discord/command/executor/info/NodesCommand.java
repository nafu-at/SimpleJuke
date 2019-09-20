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
import lavalink.client.io.jda.JdaLavalink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class NodesCommand extends CommandExecutor {

    public NodesCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (Main.getController().getConfig().getAdvancedConfig().isUseNodeServer()) {
            JdaLavalink lavalink = Main.getController().getLavalink();
            if (command.getArgs().length == 0) {
                StringBuilder builder = new StringBuilder();
                List<LavalinkSocket> nodes = lavalink.getNodes();
                builder.append("このBotには" + nodes.size() + "個のノードが登録されています。\n");
                builder.append("```");
                for (int i = 0; nodes.size() > i; i++) {
                    LavalinkSocket node = nodes.get(i);
                    builder.append("\nNo." + i);
                    builder.append("\nNodeName: " + node.getName() + "\n");
                    builder.append("Address: " + node.getRemoteUri() + "\n");
                    builder.append("Memory: " + fromByteToMB(node.getStats().getMemUsed()) + "MB / " + fromByteToMB(node.getStats().getMemReservable()) + "MB\n");
                    builder.append("Players: " + node.getStats().getPlayingPlayers() + " / " + node.getStats().getPlayers() + "\n");
                }
                builder.append("```");
                command.getChannel().sendMessage(builder.toString()).queue();
            } else switch (command.getArgs()[0]) {
                case "add":
                    try {
                        if (command.getArgs().length == 3) {
                            lavalink.addNode(new URI(command.getArgs()[1]),
                                    command.getArgs()[2]);
                        } else if (command.getArgs().length == 4) {
                            Main.getController().getLavalink().addNode(command.getArgs()[1],
                                    new URI(command.getArgs()[2]), command.getArgs()[3]);
                        }
                        command.getMessage().delete().submit();
                        command.getChannel().sendMessage("Node No." + (lavalink.getNodes().size() - 1) + " が追加されました。").queue();
                    } catch (URISyntaxException e) {
                        command.getChannel().sendMessage("入力されたURIが正しくありません！").queue();
                    }
                    break;

                case "remove":
                    try {
                        Main.getController().getLavalink().removeNode(Integer.parseInt(command.getArgs()[1]));
                        command.getChannel().sendMessage("ノードを削除しました。").queue();
                    } catch (NumberFormatException e) {
                        command.getChannel().sendMessage("ノード番号を指定して下さい。").queue();
                    }
            }
        } else {
            command.getChannel().sendMessage("このBotはノードが有効化されていません！").queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%nodes Botに接続されているLavaLinkノード一覧を表示します。\n" +
                "%prefix%nodes add [address] [password] Botにノードを追加します。これは再起動後保持されません。\n" +
                "%prefix%nodes add [name] [address] [password] Botにノードを追加します。これは再起動後保持されません。\n" +
                "%prefix%nodes remove <Number> 指定したノードを削除します。これは再起動後保持されません。```";
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
