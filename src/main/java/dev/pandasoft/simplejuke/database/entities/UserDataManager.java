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

package dev.pandasoft.simplejuke.database.entities;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.database.UserDataTableManager;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.http.discord.DiscordAPIClient;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserDataManager {
    private final UserDataTableManager tableManager;
    private final Map<User, Map<Long, CommandPermission>> dataCache = new HashMap<>();

    public UserDataManager(UserDataTableManager tableManager) throws SQLException {
        this.tableManager = tableManager;
        tableManager.makeTable();
        tableManager.cleanUp();
    }

    public Map<Long, CommandPermission> getUserData(User user) {
        return dataCache.computeIfAbsent(user, key -> {
            try (ObjectInput in = new ObjectInputStream(tableManager.getSettings(user))) {
                return (Map<Long, CommandPermission>) in.readObject();
            } catch (SQLException | IOException e) {
                log.error("データベースからデータの取得中にエラーが発生しました。", e);
            } catch (ClassNotFoundException e) {
                log.error("格納されているデータの方が正しくありません。", e);
            } catch (NullPointerException e) {
                log.debug("ユーザーデータが保存されていないため新規生成されました。: {}", user.getName());
                return new HashMap<>();
            }
            return null;
        });
    }

    public CommandPermission getUserPermission(Member member) {
        Map<Long, CommandPermission> permissions = getUserData(member.getUser());
        if (permissions == null)
            return null;

        CommandPermission permission = permissions.get(member.getGuild().getIdLong());

        if (permission == null) {
            String ownerId = "";
            try {
                ownerId =
                        new DiscordAPIClient().getBotApplicationInfo(Main.getController().getConfig().getBasicConfig().getDiscordToken()).getOwner().getID();
            } catch (IOException e) {
                log.error("Bot情報の取得中にエラーが発生しました。", e);
            }

            if (ownerId.equals(member.getUser().getId()))
                permission = CommandPermission.BOT_OWNER;
            else if (Main.getController().getConfig().getBasicConfig().getBotAdmins().contains(member.getUser().getIdLong()))
                return CommandPermission.BOT_ADMIN;
            else if (member.isOwner())
                permission = CommandPermission.GUILD_OWNER;
            else
                permission = CommandPermission.USER;
            try {
                setUserPermission(member, permission);
            } catch (SQLException | IOException e) {
                log.error("ユーザーデータの更新中にエラーが発生しました。", e);
            }
        }
        return permission;
    }

    public void setUserPermission(Member member, CommandPermission permission) throws IOException, SQLException {
        Map<Long, CommandPermission> permissions = getUserData(member.getUser());
        permissions.put(member.getGuild().getIdLong(), permission);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(permissions);
            outputStream.flush();
            tableManager.saveData(member.getUser().getIdLong(), member.getUser().getAsTag(),
                    new ByteArrayInputStream(baos.toByteArray()));
        }
    }
}
