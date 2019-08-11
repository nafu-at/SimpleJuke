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
import dev.pandasoft.simplejuke.database.entities.GuildSettingsManager;
import dev.pandasoft.simplejuke.database.entities.UserDataManager;
import dev.pandasoft.simplejuke.discord.command.CommandManager;
import dev.pandasoft.simplejuke.modules.ModuleManager;
import dev.pandasoft.simplejuke.modules.ModuleRegistry;
import dev.pandasoft.simplejuke.util.StateUpdateAgent;
import dev.pandasoft.simplejuke.util.UpdateInfoReader;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.bot.sharding.ShardManager;

public class BotController {
    protected final SimpleJukeConfig config;
    protected final DatabaseConnector databaseConnector;
    protected final GuildSettingsManager guildSettingsManager;
    protected final UserDataManager userDataManager;

    protected final ShardManager shardManager;
    protected final JdaLavalink lavalink;

    protected final CommandManager commandManager;
    protected final AudioPlayerRegistry playerRegistry;
    protected final ModuleRegistry moduleRegistry;
    protected final ModuleManager moduleManager;
    protected final StateUpdateAgent updateAgent;
    protected final UpdateInfoReader infoReader;

    protected BotController(SimpleJukeConfig config, DatabaseConnector sqlConnector,
                            GuildSettingsManager guildSettingsManager, UserDataManager userDataManager,
                            ShardManager shardManager, JdaLavalink lavalink, CommandManager commandManager,
                            AudioPlayerRegistry playerRegistry, ModuleRegistry moduleRegistry,
                            ModuleManager moduleManager, StateUpdateAgent updateAgent) {
        this.config = config;
        this.databaseConnector = sqlConnector;
        this.guildSettingsManager = guildSettingsManager;
        this.userDataManager = userDataManager;
        this.shardManager = shardManager;
        this.lavalink = lavalink;
        this.commandManager = commandManager;
        this.playerRegistry = playerRegistry;
        this.moduleRegistry = moduleRegistry;
        this.moduleManager = moduleManager;
        this.updateAgent = updateAgent;
        this.infoReader = new UpdateInfoReader(config.getAdvancedConfig().getUpdateInfoUrl());
    }

    public SimpleJukeConfig getConfig() {
        return config;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public GuildSettingsManager getGuildSettingsManager() {
        return guildSettingsManager;
    }

    public UserDataManager getUserDataManager() {
        return userDataManager;
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

    public UpdateInfoReader getInfoReader() {
        return infoReader;
    }
}
