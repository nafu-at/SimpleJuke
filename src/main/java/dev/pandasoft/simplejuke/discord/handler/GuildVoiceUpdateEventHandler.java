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

package dev.pandasoft.simplejuke.discord.handler;

import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.audio.GuildAudioPlayer;
import dev.pandasoft.simplejuke.database.entities.GuildSettings;
import dev.pandasoft.simplejuke.util.MessageUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildVoiceUpdateEventHandler extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
            for (Member member : event.getChannelLeft().getMembers())
                if (!member.getUser().isBot())
                    return;

            GuildSettings settings = Main.getController().getGuildSettingsManager().loadSettings(event.getGuild());
            if (!settings.isAutoLeave())
                return;

            MessageUtil.sendMessage(event.getGuild(), "誰も居なくなったみたいですね？");
            GuildAudioPlayer player = Main.getController().getPlayerRegistry().getGuildAudioPlayer(event.getGuild());
            player.pause(true);
            player.leaveChannel();
        }
    }
}
