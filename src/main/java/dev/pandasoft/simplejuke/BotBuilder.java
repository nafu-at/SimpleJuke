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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dev.pandasoft.simplejuke.audio.AudioPlayerRegistry;
import dev.pandasoft.simplejuke.config.LavalinkConfigSection;
import dev.pandasoft.simplejuke.config.SimpleJukeConfig;
import dev.pandasoft.simplejuke.database.DatabaseConnector;
import dev.pandasoft.simplejuke.database.GuildSettingsTableManager;
import dev.pandasoft.simplejuke.database.UserDataTableManager;
import dev.pandasoft.simplejuke.database.entities.GuildSettingsManager;
import dev.pandasoft.simplejuke.database.entities.UserDataManager;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandManager;
import dev.pandasoft.simplejuke.discord.command.executor.admin.ShutdownCommand;
import dev.pandasoft.simplejuke.discord.command.executor.admin.UpdateCommand;
import dev.pandasoft.simplejuke.discord.command.executor.admin.UpdateInfoCommand;
import dev.pandasoft.simplejuke.discord.command.executor.config.ConfigCommand;
import dev.pandasoft.simplejuke.discord.command.executor.info.HelpCommand;
import dev.pandasoft.simplejuke.discord.command.executor.info.NodesCommand;
import dev.pandasoft.simplejuke.discord.command.executor.info.StatusCommand;
import dev.pandasoft.simplejuke.discord.command.executor.info.UserInfoCommand;
import dev.pandasoft.simplejuke.discord.command.executor.module.ModuleCommand;
import dev.pandasoft.simplejuke.discord.command.executor.module.ModuleInfoCommand;
import dev.pandasoft.simplejuke.discord.command.executor.music.control.*;
import dev.pandasoft.simplejuke.discord.command.executor.music.info.ListCommand;
import dev.pandasoft.simplejuke.discord.command.executor.music.info.NowPlayingCommand;
import dev.pandasoft.simplejuke.discord.handler.GuildVoiceUpdateEventHandler;
import dev.pandasoft.simplejuke.discord.handler.MessageReceivedEventHandler;
import dev.pandasoft.simplejuke.http.discord.DiscordAPIClient;
import dev.pandasoft.simplejuke.modules.ModuleManager;
import dev.pandasoft.simplejuke.modules.ModuleRegistry;
import dev.pandasoft.simplejuke.util.GuildOwnerUpdateAgent;
import dev.pandasoft.simplejuke.util.StateUpdateAgent;
import io.sentry.Sentry;
import lavalink.client.io.jda.JdaLavalink;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BotBuilder {
    protected SimpleJukeConfig config = null;
    protected DatabaseConnector databaseConnector = null;
    protected GuildSettingsManager guildSettingsManager = null;
    protected UserDataManager userDataManager = null;

    protected Thread shutdownHookThread = null;

    protected ShardManager shardManager = null;
    protected DefaultShardManagerBuilder shardManagerBuilder = null;
    protected JdaLavalink lavalink = null;

    protected CommandManager commandManager = null;
    protected AudioPlayerRegistry playerRegistry = null;
    protected ModuleRegistry moduleRegistry;
    protected ModuleManager moduleManager = null;
    protected StateUpdateAgent updateAgent = null;
    protected GuildOwnerUpdateAgent ownerUpdateAgent = null;

    protected final List<CommandExecutor> getDefaultCommands() {
        List<CommandExecutor> defaultCommands = new ArrayList<>();
        defaultCommands.add(new ConfigCommand("config", "conf", "cf"));
        defaultCommands.add(new DestroyCommand("destroy", "des"));
        defaultCommands.add(new HelpCommand("help", "h"));
        defaultCommands.add(new InterruptCommand("interrupt", "in"));
        defaultCommands.add(new JoinCommand("join", "j"));
        defaultCommands.add(new LeaveCommand("leave", "lv", "disconnect"));
        defaultCommands.add(new ListCommand("list", "l"));
        defaultCommands.add(new ModuleCommand("module", "mod"));
        defaultCommands.add(new ModuleInfoCommand("moduleinfo", "minfo"));
        defaultCommands.add(new NodesCommand("nodes"));
        defaultCommands.add(new NowPlayingCommand("nowplaying", "np"));
        defaultCommands.add(new PauseCommand("pause", "ps"));
        defaultCommands.add(new PlayCommand("play", "p"));
        defaultCommands.add(new RepeatCommand("repeat", "r"));
        defaultCommands.add(new RePlayCommand("replay", "rep"));
        defaultCommands.add(new RequeueCommand("requeue", "req"));
        defaultCommands.add(new ReShuffleCommand("reshuffle", "resh"));
        defaultCommands.add(new SeekCommand("seek"));
        defaultCommands.add(new ShuffleCommand("shuffle", "sh"));
        defaultCommands.add(new SkipCommand("skip", "sk"));
        defaultCommands.add(new StatusCommand("status", "stats"));
        defaultCommands.add(new StopCommand("stop", "st", "s"));
        defaultCommands.add(new UserInfoCommand("userinfo", "user"));
        defaultCommands.add(new VolumeCommand("volume", "vol", "v"));

        defaultCommands.add(new ShutdownCommand("shutdown", "exit"));
        defaultCommands.add(new UpdateInfoCommand("updateinfo", "uinfo"));
        defaultCommands.add(new UpdateCommand("update"));
        return defaultCommands;
    }

    public BotBuilder setConfig(SimpleJukeConfig config) {
        this.config = config;
        return this;
    }

    public BotBuilder setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        return this;
    }

    public BotBuilder setGuildSettingsManager(GuildSettingsManager guildSettingsManager) {
        this.guildSettingsManager = guildSettingsManager;
        return this;
    }

    public BotBuilder setUserDataManager(UserDataManager userDataManager) {
        this.userDataManager = userDataManager;
        return this;
    }

    public BotBuilder setShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
        return this;
    }

    public BotBuilder setShardManagerBuilder(DefaultShardManagerBuilder defaultShardManagerBuilder) {
        this.shardManagerBuilder = defaultShardManagerBuilder;
        return this;
    }

    public BotBuilder setLavalink(JdaLavalink lavalink) {
        this.lavalink = lavalink;
        return this;
    }

    public BotBuilder setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public BotBuilder setPlayerRegistry(AudioPlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
        return this;
    }

    public BotBuilder setModuleRegistry(ModuleRegistry moduleRegistry) {
        this.moduleRegistry = moduleRegistry;
        return this;
    }

    public BotBuilder setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        return this;
    }

    public BotBuilder setUpdateAgent(StateUpdateAgent updateAgent) {
        this.updateAgent = updateAgent;
        return this;
    }

    public BotBuilder setOwnerUpdateAgent(GuildOwnerUpdateAgent ownerUpdateAgent) {
        this.ownerUpdateAgent = ownerUpdateAgent;
        return this;
    }

    protected BotController build() throws SQLException, LoginException, InterruptedException {
        if (config == null) {
            SimpleJukeConfigLoader loader = new SimpleJukeConfigLoader();
            if (!loader.checkUpdate())
                log.warn("設定ファイルが最新ではありません！設定ファイルを再生成してください。");
            config = loader.reloadConfig();

            if (config == null)
                System.exit(1);
            log.info("SimpleJukeConfig Loading... OK!");
        }

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Logger jdaLogger = (Logger) LoggerFactory.getLogger("net.dv8tion");
        Logger cpLogger = (Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        Level level = Level.valueOf(config.getAdvancedConfig().getLogLevel());
        root.setLevel(level);
        jdaLogger.setLevel(level);
        cpLogger.setLevel(level);

        if (config.getAdvancedConfig().getSentryDsn() != null && !config.getAdvancedConfig().getSentryDsn().isEmpty()) {
            Sentry.init(config.getAdvancedConfig().getSentryDsn());
        }

        if (moduleRegistry == null)
            moduleRegistry = new ModuleRegistry();

        if (moduleManager == null)
            moduleManager = new ModuleManager(moduleRegistry, "modules");
        moduleManager.loadAllModules();
        log.info("BotModule Loading... OK!");

        if (shutdownHookThread == null) {
            shutdownHookThread = new Thread(() -> {
                log.info("プログラムを終了しています...");
                if (databaseConnector != null) {
                    databaseConnector.close();
                }
                if (lavalink != null) {
                    lavalink.shutdown();
                }
                if (shardManager != null) {
                    shardManager.shutdown();
                }
                if (moduleManager != null) {
                    moduleManager.disableAllModules();
                }
            });

        }
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);

        if (databaseConnector == null) {
            databaseConnector = new DatabaseConnector(
                    config.getBasicConfig().getDatabase().getDatabaseType(),
                    config.getBasicConfig().getDatabase().getAddress(),
                    config.getBasicConfig().getDatabase().getDatabase(),
                    config.getBasicConfig().getDatabase().getUsername(),
                    config.getBasicConfig().getDatabase().getPassword());
            log.info("Database Connection... OK!");
        }

        if (guildSettingsManager == null) {
            guildSettingsManager = new GuildSettingsManager(new GuildSettingsTableManager(databaseConnector,
                    config.getBasicConfig().getDatabase().getTablePrefix() + "guild"));
        }
        if (userDataManager == null) {
            userDataManager = new UserDataManager(new UserDataTableManager(databaseConnector,
                    config.getBasicConfig().getDatabase().getTablePrefix() + "userdata"));
        }

        if (shardManager == null) {
            if (shardManagerBuilder == null) {
                shardManagerBuilder = new DefaultShardManagerBuilder().setToken(config.getBasicConfig().getDiscordToken());
            }
            shardManagerBuilder.addEventListeners(new GuildVoiceUpdateEventHandler());
            shardManagerBuilder.addEventListeners(new MessageReceivedEventHandler());

            try {
                if (config.getAdvancedConfig().isUseNodeServer()) {
                    if (lavalink == null) {
                        lavalink =
                                new JdaLavalink(new DiscordAPIClient().getBotApplicationInfo(config.getBasicConfig().getDiscordToken()).getID(),
                                        getShardsTotal(), shardId -> getJdaFromId(shardId));
                    }

                    for (LavalinkConfigSection node : config.getAdvancedConfig().getNodesInfo())
                        lavalink.addNode(node.getNodeName(), URI.create(node.getAddress()), node.getPassword());
                    shardManagerBuilder.addEventListeners(lavalink);
                    shardManagerBuilder.setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor());
                    log.info("LavaLink Connecting... OK!");
                }
            } catch (IOException e) {
                log.error("LavaLinkのロード中にエラーが発生しました。");
            }

            shardManager = shardManagerBuilder.build();
            log.info("Starting Discord Client...");
        }


        while (!shardManager.getStatus(0).equals(JDA.Status.CONNECTED)) {
            Thread.sleep(100);
        }
        log.debug("Connection Status: {} (Ping is {}ms)", shardManager.getStatus(0).toString(),
                shardManager.getShardById(0).getGatewayPing());
        log.info("Discord API Login... OK!");

        if (commandManager == null) {
            commandManager = new CommandManager(moduleRegistry);
        }

        getDefaultCommands().forEach(executor -> commandManager.getCommandRegistry(null).registerCommand(executor));

        if (playerRegistry == null) {
            playerRegistry = new AudioPlayerRegistry(new DefaultAudioPlayerManager());
        }

        if (updateAgent == null) {
            updateAgent = new StateUpdateAgent();
        }

        if (ownerUpdateAgent == null) {
            ownerUpdateAgent = new GuildOwnerUpdateAgent();
        }

        return new BotController(config, databaseConnector, guildSettingsManager, userDataManager, shardManager,
                lavalink, commandManager, playerRegistry, moduleRegistry, moduleManager, updateAgent, ownerUpdateAgent);
    }

    private JDA getJdaFromId(int shardId) {
        if (shardManager != null)
            return shardManager.getShardById(shardId);
        return null;
    }

    private int getShardsTotal() {
        if (shardManager != null)
            return shardManager.getShardsTotal();
        return 1;
    }
}