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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, CommandExecutor> commands = new LinkedHashMap<>();

    /**
     * コマンドを登録します。
     *
     * @param executor 登録するコマンドクラス
     */
    public void registerCommand(CommandExecutor executor) {
        String name = executor.getName();
        commands.put(name, executor);
        for (String alias : executor.getAliases())
            commands.put(alias, executor);
    }

    /**
     * モジュールに紐付けられた特定のコマンドを削除します。
     *
     * @param name コマンドの名前
     */
    public void removeCommand(String name) {
        commands.remove(name);
    }

    /**
     * モジュールに紐付けられた特定のコマンドを削除します。
     *
     * @param executor コマンド実行クラス
     */
    public void removeCommand(CommandExecutor executor) {
        commands.remove(executor.getName());
        executor.getAliases().forEach(commands::remove);
    }

    /**
     * モジュールに紐付けられたコマンドをすべて削除します。
     */
    public void removeCommands() {
        commands.clear();
    }

    /**
     * 登録されている全てのコマンドの一覧を返します。
     *
     * @return 登録されている全てのコマンド
     */
    public List<CommandExecutor> getCommands() {
        return new ArrayList<>(commands.values());
    }

    /**
     * 名前から対応するコマンドクラスを取得します。
     *
     * @param name 取得したいコマンド名
     * @return 対応するコマンドクラス
     */
    public CommandExecutor getExecutor(String name) {
        return commands.get(name);
    }
}
