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

import dev.pandasoft.simplejuke.modules.BotModule;

import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistry {
    private final Map<BotModule, Map<String, CommandExecutor>> commands = new LinkedHashMap<>();

    /**
     * コマンドを登録します。
     *
     * @param executor 登録するコマンドクラス
     */
    public void registerCommand(CommandExecutor executor, BotModule module) {
        Map executors = commands.computeIfAbsent(module, key -> new HashMap<>());
        String name = executor.getName();
        executors.put(name, executor);
        for (String alias : executor.getAliases())
            executors.put(alias, executor);
    }

    /**
     * モジュールに紐付けられたコマンドをすべて削除します。
     *
     * @param module 削除するコマンドに紐付けられたモジュール
     */
    public void removeCommands(BotModule module) {
        commands.remove(module);
    }

    /**
     * モジュールに紐付けられた特定のコマンドを削除します。
     *
     * @param name   コマンドの名前
     * @param module 削除するコマンドに紐付けられたモジュール
     */
    public void removeCommand(String name, BotModule module) {
        commands.get(module).remove(name);
    }

    /**
     * モジュールに紐付けられた特定のコマンドを削除します。
     *
     * @param executor コマンド実行クラス
     * @param module   削除するコマンドに紐付けられたモジュール
     */
    public void removeCommand(CommandExecutor executor, BotModule module) {
        commands.get(module).remove(executor.getName());
        executor.getAliases().forEach(commands::remove);
    }

    /**
     * 登録されている全てのコマンドの一覧を返します。
     *
     * @return 登録されている全てのコマンド
     */
    public List<CommandExecutor> getCommands() {
        List<CommandExecutor> list = new ArrayList<>();
        commands.entrySet().forEach(entry -> list.addAll(entry.getValue().values()));
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 名前から対応するコマンドクラスを取得します。
     *
     * @param name 取得したいコマンド名
     * @return 対応するコマンドクラス
     */
    public CommandExecutor getExecutor(String name) {
        List<Map.Entry<BotModule, Map<String, CommandExecutor>>> modules = new ArrayList(commands.entrySet());
        CommandExecutor executor = modules.get(0).getValue().get(name);
        for (int i = commands.size() - 1; i > 0; i--) {
            if (executor != null)
                break;
            executor = modules.get(i).getValue().get(name);
        }
        return executor;
    }
}
