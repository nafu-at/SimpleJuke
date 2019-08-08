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

import java.util.HashMap;
import java.util.Map;

public class CommandTempRegistry {
    private CommandTempRegistry() {
        throw new IllegalStateException();
    }

    private static final Map<Guild, Object> registry = new HashMap<>();

    public static void registerTemp(Guild guild, Object object) {
        registry.put(guild, object);
    }

    public static boolean equalsTemp(Guild guild, Object object) {
        return object.equals(registry.get(guild));
    }

    public static Object getTempObject(Guild guild) {
        return registry.get(guild);
    }

    public static Object removeTemp(Guild guild) {
        return registry.remove(guild);
    }
}
