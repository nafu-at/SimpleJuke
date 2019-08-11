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
import dev.pandasoft.simplejuke.modules.ModuleRegistry;

import java.util.List;

public class CommandManager {
    private final CommandRegistry botCommands;
    private final ModuleRegistry moduleRegistry;

    public CommandManager(ModuleRegistry moduleRegistry) {
        this.botCommands = new CommandRegistry();
        this.moduleRegistry = moduleRegistry;
    }

    /**
     * モジュールに紐付けられたコマンドレジストリを取得します。
     * 引数にNullが指定されている場合はBot本体に紐付けられたコマンドレジストリを返します。
     *
     * @param module コマンドレジストリを取得したいモジュール
     * @return コマンドレジストリ
     */
    public CommandRegistry getCommandRegistry(BotModule module) {
        if (module == null)
            return botCommands;
        return module.getCommandRegistry();
    }

    /**
     * 名前から対応するコマンドクラスを取得します。<br>
     * 最初にBot本体に紐付けられたコマンドレジストリから該当するコマンドを検索します。<br>
     * 見つからなかった場合はモジュールに紐付けられたコマンドレジストリを登録された順とは逆から検索します。<br>
     * 見つからなかった場合はNullを返します。
     *
     * @param name 取得したいコマンド名
     * @return 対応するコマンドクラス
     */
    public CommandExecutor getExecutor(String name) {
        CommandExecutor executor;
        executor = botCommands.getExecutor(name);
        if (executor != null)
            return executor;

        List<BotModule> modules = moduleRegistry.getModules();
        for (int i = modules.size() - 1; i >= 0; i--) {
            if (executor != null)
                break;
            executor = modules.get(i).getCommandRegistry().getExecutor(name);
        }
        return executor;
    }
}
