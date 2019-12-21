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

package dev.pandasoft.simplejuke.database.legacy;

import dev.pandasoft.simplejuke.database.DatabaseConnector;
import net.dv8tion.jda.api.entities.Guild;

import java.io.InputStream;
import java.sql.*;

public class GuildSettingsTableManager {
    private final String tablename;

    private final DatabaseConnector connector;

    public GuildSettingsTableManager(DatabaseConnector connector, String tablename) {
        this.connector = connector;
        this.tablename = tablename;
    }

    public void makeTable() throws SQLException {
        try (Connection connection = connector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tablename + " (updateDate DATE, guild CHAR(18) NOT NULL PRIMARY " +
                            "KEY, settings LONGBLOB)")) {
                ps.execute();
            }
        }
    }

    public void saveSettings(Guild guild, InputStream settings) throws SQLException {
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "REPLACE INTO " + tablename + " (updateDate, guild, settings) VALUES (?, ?, ?)")) {
            java.util.Date date = new java.util.Date();
            ps.setDate(1, new Date(date.getTime()));
            ps.setString(2, guild.getId());
            ps.setBinaryStream(3, settings);
            ps.execute();
        }
    }

    public InputStream getSettings(Guild guild) throws SQLException {
        InputStream result = null;
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT settings FROM " + tablename + " WHERE guild = ?")) {
            ps.setString(1, guild.getId());
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    result = resultSet.getBinaryStream("settings");
            }
        }
        return result;
    }

    public void cleanUp() throws SQLException {
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tablename + " WHERE updateDate < " +
                     "DATE_SUB(CURRENT_DATE(),INTERVAL 6 MONTH)")) {
            ps.executeQuery();
        }
    }
}
