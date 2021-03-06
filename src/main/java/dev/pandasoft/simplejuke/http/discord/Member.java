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

public class Member {
    private long membershipState;
    private String[] permissions;
    private String teamID;
    private Owner user;

    @JsonProperty("membership_state")
    public long getMembershipState() {
        return membershipState;
    }

    @JsonProperty("permissions")
    public String[] getPermissions() {
        return permissions;
    }

    @JsonProperty("team_id")
    public String getTeamID() {
        return teamID;
    }

    @JsonProperty("user")
    public Owner getUser() {
        return user;
    }
}
