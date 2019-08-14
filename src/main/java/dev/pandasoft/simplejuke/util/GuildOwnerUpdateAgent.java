package dev.pandasoft.simplejuke.util;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class GuildOwnerUpdateAgent implements Runnable {

    @Override
    public void run() {
        log.debug("Starting UserInfo Updater");
        Main.getController().getShardManager().getGuilds().forEach(guild -> {
            for (Member member : guild.getMembers()) {
                CommandPermission permission =
                        Main.getController().getUserDataManager().getUserPermission(member);
                if (permission.equals(CommandPermission.BOT_OWNER) || permission.equals(CommandPermission.BOT_ADMIN))
                    continue;

                try {
                    if (member.isOwner()) {
                        if (!permission.equals(CommandPermission.GUILD_OWNER))
                            Main.getController().getUserDataManager().setUserPermission(member, CommandPermission.USER);
                    } else {
                        if (!permission.equals(CommandPermission.USER))
                            Main.getController().getUserDataManager().setUserPermission(member, CommandPermission.USER);
                    }
                } catch (SQLException | IOException e) {
                    log.error("ユーザー情報の更新中にエラーが発生しました。", e);
                }
            }
        });
        log.debug("Update Finished!");
    }
}
