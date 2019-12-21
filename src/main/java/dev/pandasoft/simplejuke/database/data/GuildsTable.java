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

package dev.pandasoft.simplejuke.database.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.pandasoft.simplejuke.database.DatabaseConnector;
import dev.pandasoft.simplejuke.database.DatabaseTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * ギルドに関する情報が保持されるテーブルです。
 * | guildId | guildname | ownerId |
 */
public class GuildsTable extends DatabaseTable {

    public GuildsTable(String prefix, String tablename, DatabaseConnector connector) {
        super(prefix, tablename, connector);
    }

    public GuildsTable(String prefix, DatabaseConnector connector) {
        this(prefix, "guilds", connector);
    }

    /**
     * 以下の形式に基づいたテーブルが作成されます。<br>
     * <table><tbody>
     *     <tr><td>Name</td><td>Type</td><td>Null</td></tr>
     *     <tr><td>guildId</td><td>BIGINT</td><td>NOT NULL</td></tr>
     *     <tr><td>guildname/td><td>VARCHAR(100)</td><td>NOT NULL</td></tr>
     *     <tr><td>ownerId</td><td>BIGINT</td><td>NOT NULL</td></tr>
     * </tbody></table>
     * @throws SQLException テーブルの作成に失敗した場合にスローされます。
     */
    public void createTable() throws SQLException {
        super.createTable("guildId BIGINT NOT NULL PRIMARY KEY, " +
                "guildname VARCHAR(100) NOT NULL, ownerId BIGINT NOT NULL");
    }

    /**
     * ギルド名からギルドIDを検索します。
     * @param guildname 検索するギルド名
     * @return 取得したギルドID
     * @throws SQLException ギルドの検索に失敗した場合にスローされます。
     */
    public long searchGuildIdFromName(String guildname) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT guildId FROM " + getTablename() + " WHERE guildname = ?")) {
            ps.setString(1, guildname);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getLong("guildId");
                return 0;
            }
        }
    }

    /**
     * ギルドのギルド名を取得します。
     * @param guild 取得するギルドのギルドID
     * @return ギルド名
     * @throws SQLException ギルドの取得に失敗した場合にスローされます。
     */
    public String getGuildname(long guild) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT username FROM " + getTablename() + " WHERE guildId = ?")) {
            ps.setLong(1, guild);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getString("guildname");
                return null;
            }
        }
    }

    /**
     * 新しいギルドデータを登録します。ギルドが既に存在する場合は処理を実行せずに終了します。
     * @param guildId 登録するギルドのギルドID
     * @param guildname ギルド名
     * @param ownerId ギルドオーナーのユーザーID
     * @throws SQLException ユーザーデータの追加に失敗した場合にスローされます。
     */
    public void registerUser(long guildId, String guildname, long ownerId) throws SQLException {
        if (getGuildname(guildId) == null) {
            try (Connection connection = getConnector().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "INSERT INTO " + getTablename() + " VALUES (?, ?, ?, NULL)")) {
                ps.setLong(1, guildId);
                ps.setString(2, guildname);
                ps.setLong(3, ownerId);
                ps.execute();
            }
        }
    }

    // TODO: 2019/12/21 ギルドデータの削除について検討する
}
