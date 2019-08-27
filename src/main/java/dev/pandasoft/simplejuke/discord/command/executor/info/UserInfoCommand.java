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
                Main.getController().getUserDataManager().getUserPermission(member).name(), true));
        return builder.build();
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
