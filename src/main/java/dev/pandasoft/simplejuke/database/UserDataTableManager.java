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

import net.dv8tion.jda.core.entities.User;

import java.io.InputStream;
import java.sql.*;

public class UserDataTableManager {
    private final String tablename;
    private final DatabaseConnector connector;

    public UserDataTableManager(DatabaseConnector connector, String tablename) {
        this.connector = connector;
        this.tablename = tablename;
    }

    public void makeTable() throws SQLException {
        try (Connection connection = connector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tablename + " (updateDate DATE, userIdLong CHAR(18) NOT NULL " +
                            "PRIMARY KEY, userId VARCHAR(128), userdata LONGBLOB)")) {
                ps.execute();
            }
        }
    }

    public void saveData(long userIdLong, String userId, InputStream stream) throws SQLException {
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "REPLACE INTO " + tablename + " (updateDate, userIdLong, userId, userdata) VALUES (?, ?, ?, ?)")) {
            java.util.Date date = new java.util.Date();
            ps.setDate(1, new Date(date.getTime()));
            ps.setLong(2, userIdLong);
            ps.setString(3, userId);
            ps.setBinaryStream(4, stream);
            ps.execute();
        }
    }

    public InputStream getSettings(User user) throws SQLException {
        InputStream result = null;
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT userdata FROM " + tablename + " WHERE userIdLong = ?")) {
            ps.setLong(1, user.getIdLong());
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    result = resultSet.getBinaryStream("userdata");
            }
        }
        return result;
    }

    public void cleanUp() throws SQLException {
        try (Connection connection = connector.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tablename + " WHERE updateDate < " +
                     "DATE_SUB(CURRENT_DATE(),INTERVAL 3 MONTH)")) {
            ps.executeQuery();
        }
    }
}
