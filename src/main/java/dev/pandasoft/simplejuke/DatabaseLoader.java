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
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

@Slf4j
class DatabaseLoader extends Thread {
    private final BotBuilder botBuilder;

    public DatabaseLoader(BotBuilder botBuilder) {
        this.botBuilder = botBuilder;
    }

    @Override
    public void run() {
        // データ整合性チェック開始
        if (botBuilder.shardManager == null)
            throw new IllegalStateException("整合性チェッカーがDiscord APIとの接続確立前に呼び出されました。");
        log.info("データベースの整合性確認を開始します...");

        // Bot停止中のBotのギルド入退室を確認

        List<Guild> guilds = botBuilder.shardManager.getGuilds();
        for (Guild guild : guilds) {

        }

    }
}
