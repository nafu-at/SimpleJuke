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

package dev.pandasoft.simplejuke.discord.command.executor.module;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.modules.BotModule;
import dev.pandasoft.simplejuke.modules.ModuleDescription;
import dev.pandasoft.simplejuke.modules.ModuleRegistry;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.List;

public class ModuleInfoCommand extends CommandExecutor {

    public ModuleInfoCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        ModuleRegistry moduleRegistry = Main.getController().getModuleRegistry();
        if (command.getArgs().length == 0) {
            StringBuilder builder = new StringBuilder();
            List<BotModule> modules = moduleRegistry.getModules();
            builder.append(modules.size() + "個のモジュールがロードされています。\n```");
            modules.forEach(module -> builder.append(module.getDescription().getName() + " : v" + module.getDescription().getVersion()));
            builder.append("```");
            command.getChannel().sendMessage(builder.toString()).queue();
        } else {
            BotModule module = moduleRegistry.getModule(command.getArgs()[0]);
            if (module != null) {
                ModuleDescription description = module.getDescription();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(description.getName());
                builder.setDescription(description.getDescription());
                StringBuilder authorBuilder = new StringBuilder();
                description.getAuthors().forEach(author -> authorBuilder.append(author + " "));
                builder.setFooter(authorBuilder.toString(), null);
                builder.addField(new MessageEmbed.Field("Version", description.getVersion(), true));
                builder.addField(new MessageEmbed.Field("Website", description.getWebsite(), true));
                command.getChannel().sendMessage(builder.build()).queue();
            } else {
                command.getChannel().sendMessage("このモジュールはロードされていません。").queue();
            }
        }
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.GUILD_OWNER;
    }
}
