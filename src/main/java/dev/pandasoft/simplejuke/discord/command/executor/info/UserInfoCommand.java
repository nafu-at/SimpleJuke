package dev.pandasoft.simplejuke.discord.command.executor.info;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.time.format.DateTimeFormatter;

public class UserInfoCommand extends CommandExecutor {

    public UserInfoCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        if (!command.getMessage().getMentionedMembers().isEmpty()) {
            command.getMessage().getMentionedMembers().forEach(member -> {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(member.getNickname() + "'s Information");
                builder.setThumbnail(member.getUser().getAvatarUrl());
                builder.addField(new MessageEmbed.Field("Username", member.getUser().getAsTag(), true));
                builder.addField(new MessageEmbed.Field("ID", member.getUser().getId(), true));
                builder.addField(new MessageEmbed.Field("Nickname", member.getNickname(), true));
                builder.addField(new MessageEmbed.Field("Join Date",
                        member.getJoinDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true));
                builder.addField(new MessageEmbed.Field("Account Create",
                        member.getUser().getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), true));
                builder.addField(new MessageEmbed.Field("Permission",
                        Main.getController().getUserDataManager().getUserPermission(member.getUser(),
                                member.getGuild()).name(), true));
                command.getChannel().sendMessage(builder.build()).queue();
            });
        } else if (command.getArgs().length >= 1) {
            // TODO: 2019/08/07 メンション以外のユーザー指定の方法を実装する。
        }
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.USER;
    }
}
