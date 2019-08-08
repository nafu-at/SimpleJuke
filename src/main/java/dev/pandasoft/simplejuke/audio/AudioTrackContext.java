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

package dev.pandasoft.simplejuke.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class AudioTrackContext {
    private final Guild guild;
    private final Member invoker;
    private final AudioTrack track;

    public AudioTrackContext(Guild guild, Member invoker, AudioTrack track) {
        this.guild = guild;
        this.invoker = invoker;
        this.track = track;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getInvoker() {
        return invoker;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public AudioTrackContext makeClone() {
        return new AudioTrackContext(guild, invoker, track.makeClone());
    }
}
