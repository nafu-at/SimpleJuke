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

package dev.pandasoft.simplejuke.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException();
    }

    public static void sendStackTrace(Guild guild, Throwable throwable, String... message) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        String trace = stringWriter.toString();

        StringBuilder builder = new StringBuilder();
        builder.append(":warning: 問題が発生しました。問題が続く場合は管理者に報告してください。\n");
        for (String msg : message)
            builder.append(msg + "\n");

        if (trace.length() > 1800)
            trace = trace.substring(0, 1800) + " [...}";

        builder.append("```");
        builder.append(trace);
        builder.append("```");

        MessageUtil.sendMessage(guild, builder.toString());

        StringBuilder sb = new StringBuilder();
        for (String msg : message)
            sb.append(msg + "\n");
        log.error(sb.toString(), throwable);
    }
}
