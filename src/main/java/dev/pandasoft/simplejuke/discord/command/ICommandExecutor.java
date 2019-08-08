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
     * 登録されたコマンドの名前を返します。
     *
     * @return 登録されたコマンドの名前
     */
    String getName();

    /**
     * 関連付けられたエイリアスを返します。
     *
     * @return 関連付けられたエイリアス
     */
    List<String> getAliases();

    /**
     * コマンドを実行します。
     *
     * @param command
     * @return コマンドの実行結果
     */
    void onInvoke(BotCommand command);

    /**
     * コマンドに関するヘルプを返します。
     *
     * @return コマンドに関するヘルプ
     */
    String help();

    /**
     * このコマンドを実行するために必要な権限を返します。
     *
     * @return このコマンドを実行するために必要な権限
     */
    CommandPermission getPermission();
}
