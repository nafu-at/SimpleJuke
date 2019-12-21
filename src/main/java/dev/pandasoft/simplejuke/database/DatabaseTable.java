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

import dev.pandasoft.simplejuke.Main;

import java.sql.*;

public class DatabaseTable {
    private final String tablename;
    private final DatabaseConnector connector;

    public DatabaseTable(String prefix, String tablename, DatabaseConnector connector) {
        this.tablename = prefix + tablename;
        this.connector = connector;
    }

    protected String getTablename() {
        return tablename;
    }

    protected DatabaseConnector getConnector() {
        return connector;
    }

    /**
     * 指定された構造のテーブルを作成します。既に同名のテーブルが存在する場合は処理を実行せずに終了します。
     * @param construction 作成するテーブルの構造
     * @throws SQLException テーブルの作成に失敗した場合にスローされます。
     */
    public void createTable(String construction) throws SQLException {
        try (Connection connection = connector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tablename + " (" + construction + ")")) {
                ps.execute();
            }
        }
    }

    /**
     * テーブルにカラムを追加します。
     * @param name 追加するカラムの名前
     * @param type カラムのデータ型
     * @throws SQLException カラムの追加に失敗した場合にスローされます。
     */
    public void addTableColumn(String name, String type) throws SQLException {
        try (Connection connection = connector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "ALTER TABLE " + tablename + " ADD " + name + " " + type)) {
                ps.execute();
            }
        }
    }

    /**
     * テーブルからカラムを削除します。
     * @param name 削除するカラムの名前
     * @throws SQLException カラムの削除に失敗した場合にスローされます。
     */
    public void dropTableColumn(String name) throws SQLException {
        try (Connection connection = connector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "ALTER TABLE " + tablename + " DROP " + name)) {
                ps.execute();
            }
        }
    }
}
