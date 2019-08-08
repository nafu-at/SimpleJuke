package dev.pandasoft.simplejuke.discord.handler;

import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildUpdateOwnerEventHandler extends ListenerAdapter {


    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        // TODO: 2019-08-05 オーナー権限を書き換える。 
    }
}
