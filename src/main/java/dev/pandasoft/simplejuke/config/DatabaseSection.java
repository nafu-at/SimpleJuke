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

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseSection {
    @JsonProperty("databaseType")
    private DatabaseType databaseType;
    @JsonProperty("tablePrefix")
    private String tablePrefix;
    @JsonProperty("address")
    private String address;
    @JsonProperty("database")
    private String database;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getAddress() {
        return address;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DatabaseSection{" +
                "databaseType=" + databaseType +
                ", tablePrefix='" + tablePrefix + '\'' +
                ", address='" + address + '\'' +
                ", database='" + database + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
