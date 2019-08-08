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

package dev.pandasoft.simplejuke.database.entities;

import dev.pandasoft.simplejuke.database.GuildSettingsTableManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GuildSettingsManager {
    private final GuildSettingsTableManager tableManager;
    private final Map<Guild, GuildSettings> settingsCache = new HashMap<>();

    public GuildSettingsManager(GuildSettingsTableManager settingsTableManager) throws SQLException {
        this.tableManager = settingsTableManager;
        settingsTableManager.makeTable();
        settingsTableManager.cleanUp();
    }

    public void saveSettings(Guild guild, GuildSettings settings) throws SQLException, IOException {
        settingsCache.put(guild, settings);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(settings);
            outputStream.flush();
            tableManager.saveSettings(guild, new ByteArrayInputStream(baos.toByteArray()));
        }
    }

    public GuildSettings loadSettings(Guild guild) {
        return settingsCache.computeIfAbsent(guild, key -> {
            try (ObjectInput in = new ObjectInputStream(tableManager.getSettings(key))) {
                return (GuildSettings) in.readObject();
            } catch (SQLException | IOException e) {
                log.error("データベースから設定の取得中にエラーが発生しました。", e);
            } catch (NullPointerException e) {
                return new GuildSettings();
            } catch (ClassNotFoundException e) {
                log.error("格納されているデータの方が正しくありません。", e);
            }
            return null;
        });
    }
}
