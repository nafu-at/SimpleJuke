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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class UserInfoCommand extends CommandExecutor {

    public UserInfoCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (!command.getMessage().getMentionedMembers().isEmpty()) {
            command.getMessage().getMentionedMembers().forEach(member -> command.getChannel().sendMessage(buildMemberEmbed(member)).queue());
        } else if (command.getArgs().length >= 1) {
            Arrays.stream(command.getArgs()).forEach(id -> {
                User user = command.getChannel().getJDA().getUserById(id);
                if (user == null)
                    return;
                MessageEmbed embed;
                if (command.getGuild().isMember(user)) {
                    Member member = command.getGuild().getMember(user);
                    embed = buildMemberEmbed(member);
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(user.getName() + "'s Information");
                    builder.setThumbnail(user.getAvatarUrl());
                    builder.addField(new MessageEmbed.Field("Username", user.getAsTag(), true));
                    builder.addField(new MessageEmbed.Field("ID", user.getId(), true));
                    builder.addField(new MessageEmbed.Field("Account Create",
                            user.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true));
                    embed = builder.build();
                }
                command.getChannel().sendMessage(embed).queue();
            });
        }
    }

    private MessageEmbed buildMemberEmbed(Member member) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(member.getNickname() + "'s Information");
        builder.setThumbnail(member.getUser().getAvatarUrl());
        builder.addField(new MessageEmbed.Field("Username", member.getUser().getAsTag(), true));
        builder.addField(new MessageEmbed.Field("ID", member.getUser().getId(), true));
        builder.addField(new MessageEmbed.Field("Nickname", member.getNickname(), true));
        builder.addField(new MessageEmbed.Field("Join Date",
                member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true));
        builder.addField(new MessageEmbed.Field("Account Create",
                member.getUser().getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true));
        builder.addField(new MessageEmbed.Field("Permission",
                Main.getController().getUsersTable().getUserPermission(member).name(), true));
        return builder.build();
    }

    @Override
    public String help() {
        return "%prefix%userinfo [userId] ユーザーに関する情報を表示します。";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
