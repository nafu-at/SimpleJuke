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

package dev.pandasoft.simplejuke.discord.command.executor.music.control;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class InterruptCommand extends CommandExecutor {

    public InterruptCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        GuildAudioPlayer audioPlayer = Main.getController().getPlayerRegistry().getGuildAudioPlayer(command.getGuild());
        if (command.getArgs().length >= 2) {
            if (!command.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
                VoiceChannel targetChannel = command.getInvoker().getVoiceState().getChannel();
                if (targetChannel == null) {
                    command.getChannel().sendMessage("ボイスチャンネルに接続してから実行して下さい。").queue();
                    return;
                }
                audioPlayer.joinChannel(targetChannel);
            }

            audioPlayer.pause(false);

            try {
                audioPlayer.loadItemOrdered(command.getArgs()[0], command.getInvoker(),
                        Integer.parseInt(command.getArgs()[1]));
                if (command.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE))
                    command.getMessage().delete().submit();
            } catch (NumberFormatException e) {
                command.getChannel().sendMessage("引数が正しくありません！").queue();
            }
        }
    }

    @Override
    public String help() {
        return "```%prefix%interrupt [URL] <nomber> 指定した位置に楽曲を割り込ませます。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
