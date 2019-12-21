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

package dev.pandasoft.simplejuke.database.settings;

import dev.pandasoft.simplejuke.database.DatabaseConnector;
import dev.pandasoft.simplejuke.database.DatabaseTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfigTable extends DatabaseTable {

    public ConfigTable(String prefix, String tablename, DatabaseConnector connector) {
        super(prefix, tablename, connector);
    }

    public ConfigTable(String prefix, DatabaseConnector connector) {
        this(prefix, "config", connector);
    }

    /**
     * @deprecated このクラスではこのメソッドは動作しません。実行された場合はUnsupportedOperationExceptionを返します。
     */
    @Override
    @Deprecated
    public void addTableColumn(String name, String type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated このクラスではこのメソッドは動作しません。実行された場合はUnsupportedOperationExceptionを返します。
     */
    @Override
    @Deprecated
    public void dropTableColumn(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * 以下の形式に基づいたテーブルが作成されます。<br>
     * <table><tbody>
     *     <tr><td>Name</td><td>Type</td><td>Null</td></tr>
     *     <tr><td>option_name</td><td>VARCHAR(32)</td><td>NOT NULL</td></tr>
     *     <tr><td>option_value</td><td>LONGTEXT</td><td>NULL</td></tr>
     * </tbody></table>
     * <br>
     * @throws SQLException テーブルの作成に失敗した場合にスローされます。
     */
    public void createTable() throws SQLException {
        super.createTable("option_name VARCHAR(32) NOT NULL, option_value LONGTEXT NULL");
        // ユニークインデックスを作成します。
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "CREATE UNIQUE INDEX option_name ON " + getTablename() + "(option_name)")) {
            ps.execute();
        }
    }

    /**
     * 保存されている設定を取得します。
     * @return 保存されている設定
     * @throws SQLException 設定の取得に失敗した場合にスローされます。
     */
    public Map<String, Object> getSettings() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename())) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    map.put(resultSet.getString("option_name"), resultSet.getString("option_value"));
                return map;
            }
        }
    }

    /**
     * 保存されている設定を取得します。
     * @param name 取得する設定の項目名
     * @return 保存されている設定
     * @throws SQLException 設定の取得に失敗した場合にスローされます。
     */
    public String getSetting(String name) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT option_value FROM " + getTablename() + " WHERE option_name = ?")) {
            ps.setString(1, name);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultSet.getString("option_value");
                return null;
            }
        }
    }

    /**
     * 設定を保存します。
     * @param name 設定の項目名
     * @param value 設定の設定内容
     * @throws SQLException 設定の保存に失敗したか、ギルドIDと設定項目名が同一のものが既に存在する場合にスローされます。
     */
    public void setSetting(String name, String value) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO " + getTablename() + " (option_name, option_value) VALUES (?, ?)" +
                             "ON DUPLICATE KEY UPDATE option_value = VALUES (option_value)")) {
            ps.setString(1, name);
            ps.setString(2, value);
            ps.execute();
        }
    }

    /**
     * 設定を削除します。
     * @param name 削除する設定の項目
     * @throws SQLException 設定の削除に失敗した場合にスローされます。
     */
    public void deleteSetting(String name) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM " + getTablename() + " WHERE option_name = ?")) {
            ps.setString(1, name);
            ps.execute();
        }
    }
}
