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

package dev.pandasoft.simplejuke.database;

import dev.pandasoft.simplejuke.database.data.GuildSettingsTable;
import dev.pandasoft.simplejuke.database.data.UsersTable;
import dev.pandasoft.simplejuke.database.legacy.GuildSettingsTableManager;
import dev.pandasoft.simplejuke.database.legacy.entities.GuildSettings;
import dev.pandasoft.simplejuke.database.legacy.entities.GuildSettingsManager;
import dev.pandasoft.simplejuke.database.settings.ConfigTable;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * データベースの構造に変更があった場合に自動的に更新するためのエージェントです。
 */
@Slf4j
public class DatabaseUpdateAgent {
    private static final int VERSION = 1;

    private final ConfigTable configTable;
    private final UsersTable usersTable;
    private final GuildSettingsTable guildSettingsTable;

    public DatabaseUpdateAgent(ConfigTable configTable, UsersTable usersTable, GuildSettingsTable guildSettingsTable) {
        this.configTable = configTable;
        this.usersTable = usersTable;
        this.guildSettingsTable = guildSettingsTable;
    }

    public void updateDatabase(DatabaseConnector connector, JDA jda, String prefix) {
        if (configTable == null)
            return;

        try {
            int now;
            now = Integer.parseInt(configTable.getSetting("database_version"));
            log.debug("データベースの更新があります。 現在のバージョン: {}, 最新のバージョン: {}", now, VERSION);
            if (now < VERSION) {
                if (now < 1)
                    updateDatabaseV1(connector, jda, prefix);
                // 今後新しいバージョンが追加されたら後ろに追記する。
            }
        } catch (SQLException | NumberFormatException e) {
            log.error("データベースからデータの取得に失敗しました。", e);
        }
    }

    private void updateDatabaseV1(DatabaseConnector connector, JDA jda, String prefix) {
        // unmarked to Version 1. 2019-11_V.1.1.0.0
        // update guild settings
        GuildSettingsTableManager guildSettingsManager = new GuildSettingsTableManager(connector, prefix);
        for (Guild guild : jda.getGuilds()) {
            try (ObjectInput in = new ObjectInputStream(guildSettingsManager.getSettings(guild))) {
                 GuildSettings settings = (GuildSettings) in.readObject();
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "prefix", settings.getPrefix());
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "volume", String.valueOf(settings.getVolume()));
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "repeat", settings.getRepeat().name());
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "shuffle", String.valueOf(settings.isShuffle()));
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "autoLeave", String.valueOf(settings.isAnnounce()));
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "announce", String.valueOf(settings.isAnnounce()));
                 guildSettingsTable.setGuildSetting(guild.getIdLong(), "list_range", String.valueOf(settings.getListRange()));
            } catch (SQLException | IOException | ClassNotFoundException e) {
                log.error("データの移行中にエラーが発生しました。", e);
            }
        }

        // テーブルの削除
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DROP TABLE IF EXISTS " + prefix + "guild, " + prefix + "userdata")) {
            ps.execute();
        } catch (SQLException e) {
            log.error("古いテーブルの削除に失敗しました。", e);
        }
    }
}
