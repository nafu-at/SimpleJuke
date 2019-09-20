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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

@Slf4j
public class UpdateCommand extends CommandExecutor {

    public UpdateCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public void onInvoke(BotCommand command) {
        int updateLevel = 3;
        if (command.getArgs().length != 0) {
            switch (command.getArgs()[0].toUpperCase()) {
                case "LOCAL":
                    String file;
                    if (command.getArgs().length != 1) {
                        file = command.getArgs()[1];
                    } else {
                        if (command.getMessage().getAttachments().isEmpty())
                            return;
                        file = (command.getMessage().getAttachments().get(0)).getUrl();
                    }

                    if (!updateBot(file, command))
                        command.getChannel().sendMessage(":x: Botの更新に失敗しました。").queue();

                    System.exit(0);
                    return;

                case "STABLE":
                case "BETA":
                case "ALPHA":
                case "NIGHTLY":
                    updateLevel = VersionType.valueOf(command.getArgs()[0].toUpperCase()).getLevel();
                    break;

                default:
                    command.getChannel().sendMessage("引数が正しくありません！").queue();
                    return;
            }
        }

        if (Main.getController().getInfoReader().getUpdateInfo(updateLevel).isEmpty()) {
            command.getChannel().sendMessage(":negative_squared_cross_mark: このレベルではまだリリースされていません！").queue();
            return;
        } else if (!Main.getController().getInfoReader().checkUpdate(updateLevel)) {
            command.getChannel().sendMessage(":white_check_mark: このBotは最新版です。").queue();
            return;
        }

        VersionInfo latestVersion = Main.getController().getInfoReader().getUpdateInfo(updateLevel).get(0);
        if (!latestVersion.getDownload().isEmpty()) {
            if (!updateBot(latestVersion.getDownload(), command)) {
                command.getChannel().sendMessage(":x: Botの更新に失敗しました。").queue();
            }

            System.exit(0);
        } else {
            command.getChannel().sendMessage(":link: 更新用リンクが設定されていないためこのバージョンには更新できません！").queue();
        }
    }

    private Path getApplicationPath(Class<?> cls) throws URISyntaxException {
        ProtectionDomain domain = cls.getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        URL location = source.getLocation();
        URI uri = location.toURI();
        Path path = Paths.get(uri);
        return path;
    }

    private boolean updateBot(String file, BotCommand command) {
        try {
            File temp = new File("SimpleJuke_Update.jar");
            FileUtils.copyURLToFile(new URL(file), temp);

            String temp_sha3 = DigestUtils.sha3_256Hex(new FileInputStream(temp));
            String original_sha3 =
                    DigestUtils.sha3_256Hex(new FileInputStream(getApplicationPath(Main.class).toFile()));
            if (original_sha3.equals(temp_sha3)) {
                FileUtils.forceDelete(temp);
                return true;
            }

            command.getChannel().sendMessage(":warning: 更新作業を実行します。完了したらプログラムを終了します。").queue();
            FileUtils.forceDelete(getApplicationPath(Main.class).toFile());
            FileUtils.moveFile(temp, getApplicationPath(Main.class).toFile());
            return true;
        } catch (IOException e) {
            command.getChannel().sendMessage("ファイルの書き換えに失敗しました。").queue();
            log.error("ファイルの置き換えに失敗しました。", e);
            return false;
        } catch (URISyntaxException e) {
            command.getChannel().sendMessage("JARファイルの場所の取得に失敗しました。").queue();
            return false;
        }
    }

    @Override
    public String help() {
        return "```%prefix%update Botのバージョンをアップデートします。```";
    }

    @Override
    public CommandPermission getPermission() {
        return CommandPermission.BOT_OWNER;
    }
}
