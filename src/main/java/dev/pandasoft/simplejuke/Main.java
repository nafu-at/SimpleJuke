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

import lombok.extern.slf4j.Slf4j;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
    private static BotController controller;

    public static void main(String[] args) {
        log.info("\n ____  _                 _          _       _        \n" +
                "/ ___|(_)_ __ ___  _ __ | | ___    | |_   _| | _____ \n" +
                "\\___ \\| | '_ ` _ \\| '_ \\| |/ _ \\_  | | | | | |/ / _ \\\n" +
                " ___) | | | | | | | |_) | |  __/ |_| | |_| |   <  __/\n" +
                "|____/|_|_| |_| |_| .__/|_|\\___|\\___/ \\__,_|_|\\_\\___|\n" +
                "                  |_|                                \n");
        log.info("SimpleJuke v" + Main.class.getPackage().getImplementationVersion() + " 起動しています...");

        try {
            controller = new BotBuilder().build();
        } catch (LoginException e) {
            log.error("Discord APIへのログイン中にエラーが発生しました。", e);
            System.exit(1);
        } catch (InterruptedException e) {
            log.error("何らかの理由によりログインが完了しませんでした。", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (SQLException e) {
            log.error("データベースとの通信に失敗しました。", e);
            System.exit(1);
        }

        controller.getModuleManager().enableAllModules();
        log.info("すべてのモジュールがアクティブ化されました。");

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(controller.getUpdateAgent(), 0, 1, TimeUnit.HOURS);
        ses.scheduleAtFixedRate(controller.getOwnerUpdateAgent(), 0, 1, TimeUnit.DAYS);
        ses.scheduleAtFixedRate(controller.getPlayerChecker(), 0, 30, TimeUnit.MINUTES);
    }

    public static BotController getController() {
        return controller;
    }
}
