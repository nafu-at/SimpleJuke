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

package dev.pandasoft.simplejuke.config;

public enum DatabaseType {
    MARIADB("org.mariadb.jdbc.Driver", "jdbc:mariadb://"),
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://"),
    SQLITE("org.sqlite.JDBC", "jdbc:sqlite:");

    DatabaseType(String jdbcClass, String addressPrefix) {
        this.jdbcClass = jdbcClass;
        this.addressPrefix = addressPrefix;
    }

    private final String jdbcClass;
    private final String addressPrefix;

    public String getJdbcClass() {
        return jdbcClass;
    }

    public String getAddressPrefix() {
        return addressPrefix;
    }
}
