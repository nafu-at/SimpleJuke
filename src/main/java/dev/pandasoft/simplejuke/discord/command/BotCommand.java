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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;

public class BotCommand {
    private final Guild guild;
    private final TextChannel channel;
    private final Member invoker;
    private final Message message;

    private final String trigger;
    private final String[] args;
    private final CommandExecutor command;

    public BotCommand(Guild guild,
                      TextChannel channel,
                      Member invoker,
                      Message msg,
                      String trigger,
                      String[] args,
                      CommandExecutor command) {
        this.guild = guild;
        this.channel = channel;
        this.invoker = invoker;
        this.message = msg;
        this.trigger = trigger;
        this.args = args;
        this.command = command;
    }

    /**
     * @return コマンドが実行されたギルド
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * @return コマンドが実行されたテキストチャンネル
     */
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * @return コマンドを実行したメンバー
     */
    public Member getInvoker() {
        return invoker;
    }

    /**
     * @return 実際に送信されたメッセージ
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return 実行されたコマンド名
     */
    public String getTrigger() {
        return trigger;
    }

    /**
     * @return 指定されたオプション
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * @return 送信されたコマンド名に該当するコマンドクラス
     */
    public CommandExecutor getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "BotCommand{" +
                "guild=" + guild +
                ", channel=" + channel +
                ", invoker=" + invoker +
                ", message=" + message +
                ", trigger='" + trigger + '\'' +
                ", args=" + Arrays.toString(args) +
                ", command=" + command +
                '}';
    }
}
