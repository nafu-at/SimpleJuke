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

import dev.pandasoft.simplejuke.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class BotCommandParser {

    /**
     * 受信したメッセージをBotコマンドとして使用できる形に整形します。
     *
     * @param prefix メッセージをコマンドとして認識する接頭辞
     * @param event  受信したメッセージイベント
     * @return 生成されたBotコマンド
     */
    public BotCommand parse(String prefix, MessageReceivedEvent event) {
        String raw = event.getMessage().getContentRaw();

        String input;
        // 自分宛てのメンションの場合はコマンドとして認識
        if (raw.startsWith(prefix)) {
            input = raw.substring(prefix.length()).trim();
        } else if (!event.getMessage().getMentions().isEmpty()) {
            if (!event.getMessage().isMentioned(event.getJDA().getSelfUser()))
                return null;
            input = raw.substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            // コマンドではないメッセージを無視
        } else {
            return null;
        }

        if (input.isEmpty())
            return null;

        // コマンドオプションを分割
        String[] args = input.split("\\p{javaSpaceChar}+");
        if (args.length == 0)
            return null;
        String commandTrigger = args[0];

        // コマンドクラスの取得
        CommandExecutor command = Main.getController().getCommandManager().getExecutor(commandTrigger.toLowerCase());
        if (command == null)
            return null;
        else
            return new BotCommand(
                    event.getGuild(),
                    event.getTextChannel(),
                    event.getMember(),
                    event.getMessage(),
                    commandTrigger,
                    Arrays.copyOfRange(args, 1, args.length),
                    command);
    }
}
