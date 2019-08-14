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

package dev.pandasoft.simplejuke.discord.command.executor.admin;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.discord.command.BotCommand;
import dev.pandasoft.simplejuke.discord.command.CommandExecutor;
import dev.pandasoft.simplejuke.discord.command.CommandPermission;
import dev.pandasoft.simplejuke.util.update.VersionInfo;
import dev.pandasoft.simplejuke.util.update.VersionType;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class UpdateInfoCommand extends CommandExecutor {

    public UpdateInfoCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        int updateLevel = 999;
        if (command.getArgs().length != 0) {
            updateLevel = VersionType.valueOf(command.getArgs()[0].toUpperCase()).getLevel();
        }

        if (Main.getController().getInfoReader().getUpdateInfo(updateLevel).isEmpty()) {
            command.getChannel().sendMessage(":negative_squared_cross_mark: このレベルではまだリリースされていません！").queue();
        } else {
            VersionInfo latestVersion = Main.getController().getInfoReader().getUpdateInfo(updateLevel).get(0);
            if (Main.getController().getInfoReader().checkUpdate(updateLevel))
                command.getChannel().sendMessage(":bell: Botに更新があります！Updateコマンドを実行してBotを更新して下さい！").queue();
            else
                command.getChannel().sendMessage(":white_check_mark: このBotは最新です。").queue();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(latestVersion.getTitle());
            switch (latestVersion.getLevel()) {
                case INFO:
                    builder.setColor(Color.CYAN);
                    break;
                case STABLE:
                    builder.setColor(Color.GREEN);
                    break;
                case BETA:
                    builder.setColor(Color.ORANGE);
                    break;
                case ALPHA:
                    builder.setColor(Color.RED);
                    break;
                case NIGHTLY:
                    builder.setColor(Color.BLUE);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + latestVersion.getLevel());
            }

            builder.addField("Version", latestVersion.getVersion(), true);
            builder.addField("UpdateDate", latestVersion.getUpdateDateRaw(), true);
            builder.addField("UpdateLevel", latestVersion.getLevel().name(), true);
            StringBuilder descriptionBuilder = new StringBuilder();
            latestVersion.getDescription().forEach(description -> descriptionBuilder.append("☆ " + description + "\n"));
            if (descriptionBuilder.length() > 500)
                descriptionBuilder.setLength(500);
            builder.addField("Description", descriptionBuilder.toString(), false);
            if (!latestVersion.getKnownbugs().isEmpty()) {
                StringBuilder knownBugsBuilder = new StringBuilder();
                latestVersion.getKnownbugs().forEach(knownBugs -> knownBugsBuilder.append("★ " + knownBugs + "\n"));
                if (knownBugsBuilder.length() > 500)
                    knownBugsBuilder.setLength(500);
                builder.addField("KnownBugs", knownBugsBuilder.toString(), false);
            }
            if (latestVersion.getDownload().isEmpty())
                builder.addField("AudoUpdate", "このバージョンには自動更新は提供されていません！", true);
            else
                builder.addField("AudoUpdate", "このバージョンには自動更新は提供されています", true);

            command.getChannel().sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String help() {
        return "```%prefix%updateinfo\n" + "Botの更新情報を表示します。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }
}
