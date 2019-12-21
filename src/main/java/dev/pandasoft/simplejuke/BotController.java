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

package dev.pandasoft.simplejuke;

import dev.pandasoft.simplejuke.audio.AudioPlayerRegistry;
import dev.pandasoft.simplejuke.config.SimpleJukeConfig;
import dev.pandasoft.simplejuke.database.DatabaseConnector;
import dev.pandasoft.simplejuke.database.data.GuildSettingsTable;
import dev.pandasoft.simplejuke.database.data.UsersTable;
import dev.pandasoft.simplejuke.discord.command.CommandManager;
import dev.pandasoft.simplejuke.modules.ModuleManager;
import dev.pandasoft.simplejuke.modules.ModuleRegistry;
import dev.pandasoft.simplejuke.util.GuildOwnerUpdateAgent;
import dev.pandasoft.simplejuke.util.HibernatePlayerChecker;
import dev.pandasoft.simplejuke.util.StateUpdateAgent;
import dev.pandasoft.simplejuke.util.UpdateInfoReader;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.sharding.ShardManager;

public class BotController {
    protected final SimpleJukeConfig config;
    protected final DatabaseConnector databaseConnector;
    protected final GuildSettingsTable guildSettingsTable;
    protected final UsersTable usersTable;

    protected final ShardManager shardManager;
    protected final JdaLavalink lavalink;

    protected final CommandManager commandManager;
    protected final AudioPlayerRegistry playerRegistry;
    protected final ModuleRegistry moduleRegistry;
    protected final ModuleManager moduleManager;
    protected final StateUpdateAgent updateAgent;
    protected final GuildOwnerUpdateAgent ownerUpdateAgent;
    protected final HibernatePlayerChecker playerChecker;
    protected final UpdateInfoReader infoReader;

    protected BotController(SimpleJukeConfig config, DatabaseConnector sqlConnector,
                            GuildSettingsTable guildSettingsTable, UsersTable usersTable,
                            ShardManager shardManager, JdaLavalink lavalink, CommandManager commandManager,
                            AudioPlayerRegistry playerRegistry, ModuleRegistry moduleRegistry,
                            ModuleManager moduleManager, StateUpdateAgent updateAgent,
                            GuildOwnerUpdateAgent ownerUpdateAgent, HibernatePlayerChecker playerChecker) {
        this.config = config;
        this.databaseConnector = sqlConnector;
        this.guildSettingsTable = guildSettingsTable;
        this.usersTable = usersTable;
        this.shardManager = shardManager;
        this.lavalink = lavalink;
        this.commandManager = commandManager;
        this.playerRegistry = playerRegistry;
        this.moduleRegistry = moduleRegistry;
        this.moduleManager = moduleManager;
        this.updateAgent = updateAgent;
        this.ownerUpdateAgent = ownerUpdateAgent;
        this.playerChecker = playerChecker;
        this.infoReader = new UpdateInfoReader(config.getAdvancedConfig().getUpdateInfoUrl());
    }

    public SimpleJukeConfig getConfig() {
        return config;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public GuildSettingsTable getGuildSettingsTable() {
        return guildSettingsTable;
    }

    public UsersTable getUsersTable() {
        return usersTable;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AudioPlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    public ModuleRegistry getModuleRegistry() {
        return moduleRegistry;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public StateUpdateAgent getUpdateAgent() {
        return updateAgent;
    }

    public GuildOwnerUpdateAgent getOwnerUpdateAgent() {
        return ownerUpdateAgent;
    }

    public HibernatePlayerChecker getPlayerChecker() {
        return playerChecker;
    }

    public UpdateInfoReader getInfoReader() {
        return infoReader;
    }
}
