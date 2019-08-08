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

package dev.pandasoft.simplejuke.http.discord;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordAppInfo {
    private boolean botPublic;
    private boolean botRequireCodeGrant;
    private String coverImage;
    private String description;
    private String guildID;
    private Object icon;
    private String id;
    private String name;
    private Owner owner;
    private String primarySkuID;
    private String slug;
    private String summary;
    private Team team;
    private String verifyKey;

    @JsonProperty("bot_public")
    public boolean getBotPublic() {
        return botPublic;
    }

    @JsonProperty("bot_require_code_grant")
    public boolean getBotRequireCodeGrant() {
        return botRequireCodeGrant;
    }

    @JsonProperty("cover_image")
    public String getCoverImage() {
        return coverImage;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("guild_id")
    public String getGuildID() {
        return guildID;
    }

    @JsonProperty("icon")
    public Object getIcon() {
        return icon;
    }

    @JsonProperty("id")
    public String getID() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("owner")
    public Owner getOwner() {
        return owner;
    }

    @JsonProperty("primary_sku_id")
    public String getPrimarySkuID() {
        return primarySkuID;
    }

    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("team")
    public Team getTeam() {
        return team;
    }

    @JsonProperty("verify_key")
    public String getVerifyKey() {
        return verifyKey;
    }
}
