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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pandasoft.simplejuke.database.DatabaseConnector;
import dev.pandasoft.simplejuke.database.DatabaseTable;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ユーザーに関する情報が保持されるテーブルです。
 */
@Slf4j
public class UsersTable extends DatabaseTable {
    private final ObjectMapper mapper = new ObjectMapper();

    public UsersTable(String prefix, String tablename, DatabaseConnector connector) {
        super(prefix, tablename, connector);
    }

    public UsersTable(String prefix, DatabaseConnector connector) {
        this(prefix, "users", connector);
    }

    /**
     * 以下の形式に基づいたテーブルが作成されます。<br>
     * <table><tbody>
     *     <tr><td>Name</td><td>Type</td><td>Null</td></tr>
     *     <tr><td>userId</td><td>BIGINT</td><td>NOT NULL</td></tr>
     *     <tr><td>username</td><td>VARCHAR(37)</td><td>NOT NULL</td></tr>
     *     <tr><td>accountCreate</td><td>BIGINT</td><td>NOT NULL</td></tr>
     *     <tr><td>joinedGuild</td><td>LONGTEXT</td><td>NULL</td></tr>
     * </tbody></table>
     * <br>
     * <b>usernameにはdiscriminatorが含まれた最大37文字を返します。 (例: ChocoCha#7945)</b>
     * @throws SQLException テーブルの作成に失敗した場合にスローされます。
     */
    public void createTable() throws SQLException {
        super.createTable("userId BIGINT NOT NULL PRIMARY KEY, " +
                "username VARCHAR(37) NOT NULL, accountCreate BIGINT NOT NULL, joinedGuild LONGTEXT NULL");
    }

    /**
     * 保存されているユーザーをすべて取得します。
     * @return 保存されているユーザー一覧
     * @throws SQLException データの取得に失敗した場合にスローされます。
     */
    public List<Long> getUsers() throws SQLException {
        List<Long> users = new ArrayList<>();
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT userId FROM " + getTablename())) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    users.add(resultSet.getLong("userId"));
                return users;
            }
        }
    }

    /**
     * ユーザー名からユーザーIDを検索します。
     * @param username 検索するユーザー名
     * @return 取得したユーザーID
     * @throws SQLException ユーザーの検索に失敗した場合にスローされます。
     */
    public long searchUserIdFromName(String username) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT userId FROM " + getTablename() + " WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getLong("userId");
                return 0;
            }
        }
    }

    /**
     * 保存されているユーザーデータのすべてを取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return 保存されているすべてのユーザーデータ
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public Map<String, Object> getUserData(long userId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE userId = ?")) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    ResultSetMetaData metadata = resultSet.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String columnName = metadata.getColumnName(i);
                        map.put(columnName, resultSet.getObject(columnName));
                    }
                    return map;
                }
                return null;
            }
        }
    }

    /**
     * アカウントのDiscriminatorが含まれたユーザー名を取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return アカウントのユーザー名
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public String getUsernameAsTag(long userId) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT username FROM " + getTablename() + " WHERE userId = ?")) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getString("username");
                return null;
            }
        }
    }

    /**
     * アカウントのユーザー名を取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return アカウントのユーザー名
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public String getUsername(long userId) throws SQLException {
        return getUsernameAsTag(userId).split("#")[0];
    }

    /**
     * アカウントのユーザー識別子を#と数字4桁で取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return アカウントのユーザー識別子
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public String getDiscriminator(long userId) throws SQLException {
        return getUsernameAsTag(userId).split("#")[1];
    }

    /**
     * アカウントの作成日を取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return アカウントの作成日
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public long getAccountCreateDate(long userId) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT accountCreate FROM " + getTablename() + " WHERE userId = ?")) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getLong("accountCreate");
                return 0;
            }
        }
    }

    /**
     * このユーザーが参加しているギルドID一覧をJson形式で取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return ユーザーが参加しているギルドID一覧
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public String getJoinedGuilds(long userId) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT joinedGuild FROM " + getTablename() + " WHERE userId = ?")) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getString("joinedGuild");
                return null;
            }
        }
    }

    /**
     * このユーザーが参加しているギルドID一覧を取得します。
     * @param userId 取得するユーザーのユーザーID
     * @return ユーザーが参加しているギルドID一覧
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     */
    public List getJoinedGuildsList(long userId) throws SQLException, JsonProcessingException {
        String json = getJoinedGuilds(userId);
        if (json != null)
            return mapper.readValue(json, new TypeReference<List<Long>>() {});
        return new ArrayList();
    }

    /**
     * 参加ギルド一覧に指定したギルドを追加します。
     * @param userId 変更するユーザーのユーザーID
     * @param guildId 追加するギルドのギルドID
     * @throws SQLException ユーザーデータの更新に失敗した場合にスローされます。
     * @throws JsonProcessingException 保存されているデータに問題がある場合にスローされます。
     */
    public void addJoinedGuild(long userId, long guildId) throws SQLException, JsonProcessingException {
        List<Long> guilds = getJoinedGuildsList(userId);
        if (guilds == null)
            guilds = new ArrayList<>();

        guilds.add(guildId);
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE " + getTablename() + " SET joinedGuild = ?" +
                     " WHERE userId = ?")) {
            ps.setString(1, mapper.writeValueAsString(guilds));
            ps.setLong(2, userId);
            ps.execute();
        }
    }

    /**
     * 参加ギルドから指定したギルドを削除します。
     * @param userId 変更するユーザーのユーザーID
     * @param guildId 削除するギルドのギルドID
     * @throws SQLException ユーザーデータの更新に失敗した場合にスローされます。
     * @throws JsonProcessingException 保存されているデータに問題がある場合にスローされます。
     */
    public void removeJoinedGuild(long userId, long guildId) throws SQLException, JsonProcessingException {
        List<Long> guilds = getJoinedGuildsList(userId);
        if (guilds == null)
            guilds = new ArrayList<>();

        guilds.remove(guildId);
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE " + getTablename() + " SET joinedGuild = ?" +
                     " WHERE userId = ?")) {
            if (guilds.isEmpty())
                ps.setNull(1, Types.LONGVARCHAR);
            else
                ps.setString(1, mapper.writeValueAsString(guilds));
            ps.setLong(2, userId);
            ps.execute();
        }
    }

    /**
     * ユーザーに紐付けて保存されたデータを取得します。
     * @param columnName 取得したいデータの項目名
     * @param userId 取得するユーザーのユーザーID
     * @param <T> 返されるデータの型
     * @return 取得されたデータ
     * @throws SQLException ユーザーデータの取得に失敗した場合にスローされます。
     * @throws ClassCastException データのキャストに失敗した場合にスローされます。
     */
    public <T> T getValue(String columnName, long userId) throws SQLException, ClassCastException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT ? FROM " + getTablename() + " WHERE userId = ?")) {
            ps.setString(1, columnName);
            ps.setLong(2, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return (T) resultSet.getObject(columnName);
                return null;
            }
        }
    }

    /**
     * 新しいユーザーデータを登録します。ユーザーが既に存在する場合は処理を実行せずに終了します。
     * @param userId 登録するユーザーのユーザーID
     * @param username ユーザー名
     * @param createDate アカウント作成日
     * @throws SQLException ユーザーデータの追加に失敗した場合にスローされます。
     */
    public void registerUser(long userId, String username, long createDate) throws SQLException {
        if (getUsernameAsTag(userId) == null) {
            try (Connection connection = getConnector().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "INSERT INTO " + getTablename() + " VALUES (?, ?, ?, NULL)")) {
                ps.setLong(1, userId);
                ps.setString(2, username);
                ps.setLong(3, createDate);
                ps.execute();
            }
        }
    }

    /**
     * 登録されているユーザーデータを削除します。通常は所属中のギルド情報が残っている場合は削除を実行しません。
     * @param userId 削除するユーザーのユーザーID
     * @param forceDelete データが残っている場合でも強制的に削除する
     * @throws SQLException ユーザーデータの削除に失敗した場合にスローされます。
     * @throws IllegalStateException 所属中のギルド情報が残っている状態で通常削除を実行しようとした場合にスローされます。
     */
    public void deleteUser(long userId, boolean forceDelete) throws SQLException {
        List<Long> guilds;
        try {
            guilds = getJoinedGuildsList(userId);
        } catch (JsonProcessingException e) {
            log.warn("ユーザーデータが正しくない形式で保存されています。", e);
            return;
        }

        if (guilds.isEmpty() || (!guilds.isEmpty() && forceDelete)) {
            try (Connection connection = getConnector().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "DELETE FROM " + getTablename() + " WHERE userId = ?")) {
                ps.setLong(1, userId);
                ps.execute();
            }
        } else {
            throw new IllegalStateException("ユーザーデータに所属中のギルド情報が残っています。");
        }
    }

    /**
     * ユーザーに紐付けて保存されたデータを更新します。このメソッドでは一部の予約された要素を変更することはできません。
     * @param userId データを更新するユーザーのユーザーID
     * @param columnName 更新したいデータの項目名
     * @param value 更新するデータ
     * @throws SQLException ユーザーデータの更新に失敗した場合にスローされます。
     * @throws IllegalArgumentException 予約された要素を変更しようとした場合にスローされます。
     */
    public void updateValue(long userId, String columnName, Object value) throws SQLException {
        switch (columnName) {
            case "userId":
            case "username":
            case "accountCreate":
                throw new IllegalArgumentException("この値を変更することは禁止されています。");

            case "joinedGuild":
                throw new IllegalArgumentException("この値はこのメソッドで変更することはできません。");

            default:
                try (Connection connection = getConnector().getConnection();
                     PreparedStatement ps = connection.prepareStatement(
                             "UPDATE " + getTablename() + " SET ? = ? WHERE userId = ?")) {
                    ps.setString(1, columnName);
                    ps.setObject(2, value);
                    ps.setLong(3, userId);
                    ps.execute();
                }
                break;
        }
    }

    // TODO: 2019/11/20 参加ギルド情報の追加削除処理を実装する。
}
