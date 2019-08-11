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

package dev.pandasoft.simplejuke.discord.command;

import java.util.List;

public interface ICommandExecutor {

    /**
     * @return 登録されたコマンドの名前
     */
    String getName();

    /**
     * @return 関連付けられたエイリアス
     */
    List<String> getAliases();

    /**
     * コマンドを実行します。
     *
     * @param command 実行時に使用するコマンドコンテキスト
     */
    void onInvoke(BotCommand command);

    /**
     * @return コマンドに関するヘルプ
     */
    String help();

    /**
     * @return このコマンドを実行するために必要な権限
     */
    CommandPermission getPermission();
}
