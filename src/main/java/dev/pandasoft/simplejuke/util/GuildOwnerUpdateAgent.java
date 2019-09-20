package dev.pandasoft.simplejuke.util;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class GuildOwnerUpdateAgent implements Runnable {

    @Override
    public void run() {
        log.debug("Starting UserInfo Updater");
        Main.getController().getShardManager().getGuilds().forEach(guild -> {
            for (Member member : guild.getMembers()) {
                if (member.getUser().isBot())
                    continue;

                CommandPermission userPermission =
                        Main.getController().getUserDataManager().getUserPermission(member);
                if (userPermission.equals(CommandPermission.BOT_OWNER))
                    continue;

                try {
                    CommandPermission permission;
                    if (Main.getController().getConfig().getBasicConfig().getBotAdmins().contains(member.getUser().getIdLong()))
                        permission = CommandPermission.BOT_ADMIN;
                    else if (member.isOwner())
                        permission = CommandPermission.GUILD_OWNER;
                    else
                        permission = CommandPermission.USER;

                    if (!permission.equals(userPermission))
                        Main.getController().getUserDataManager().setUserPermission(member, permission);
                } catch (SQLException | IOException e) {
                    log.error("ユーザー情報の更新中にエラーが発生しました。", e);
                }
            }
        });
        log.debug("Update Finished!");
    }
}
