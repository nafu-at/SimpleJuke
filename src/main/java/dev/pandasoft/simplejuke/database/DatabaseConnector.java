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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.pandasoft.simplejuke.config.DatabaseType;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector implements Closeable {
    private HikariDataSource dataSource;

    public DatabaseConnector(DatabaseType databaseType, String address, String database, String username,
                             String password) {
        HikariConfig hconfig = new HikariConfig();
        hconfig.setDriverClassName(databaseType.getJdbcClass());
        hconfig.setJdbcUrl(databaseType.getAddressPrefix() + address + "/" + database);
        hconfig.addDataSourceProperty("user", username);
        hconfig.addDataSourceProperty("password", password);
        dataSource = new HikariDataSource(hconfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }
}
