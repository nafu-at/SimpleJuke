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

package dev.pandasoft.simplejuke.database.entities;

import dev.pandasoft.simplejuke.Main;

import java.io.Serializable;

public class GuildSettings implements Serializable {
    private String prefix = Main.getController().getConfig().getBasicConfig().getPrefix();
    private int volume = 100;
    private RepeatSetting repeat = RepeatSetting.NONE;
    private boolean shuffle = false;
    private boolean autoLeave = true;
    private boolean announce = false;
    private int listRange = 15;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public RepeatSetting getRepeat() {
        return repeat;
    }

    public void setRepeat(RepeatSetting repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isAutoLeave() {
        return autoLeave;
    }

    public void setAutoLeave(boolean autoLeave) {
        this.autoLeave = autoLeave;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public int getListRange() {
        return listRange;
    }

    public void setListRange(int listRange) {
        this.listRange = listRange;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "prefix='" + prefix + '\'' +
                ", volume=" + volume +
                ", repeat=" + repeat +
                ", shuffle=" + shuffle +
                ", announce=" + announce +
                ", listRange=" + listRange +
                '}';
    }
}
