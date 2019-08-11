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

package dev.pandasoft.simplejuke.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AdvancedConfigSection {
    private String googleAPIToken;
    private String updateInfoUrl;
    private boolean useNodeServer;
    private List<LavalinkConfigSection> nodesInfo;
    private String logLevel;
    private String sentryDsn;

    @JsonProperty("googleAPIToken")
    public String getGoogleAPIToken() {
        return googleAPIToken;
    }

    @JsonProperty("updateInfoUrl")
    public String getUpdateInfoUrl() {
        return updateInfoUrl;
    }

    @JsonProperty("useNodeServer")
    public boolean isUseNodeServer() {
        return useNodeServer;
    }

    @JsonProperty("nodesInfo")
    public List<LavalinkConfigSection> getNodesInfo() {
        return nodesInfo;
    }

    @JsonProperty("logLevel")
    public String getLogLevel() {
        return logLevel;
    }

    @JsonProperty("sentryDsn")
    public String getSentryDsn() {
        return sentryDsn;
    }

    @Override
    public String toString() {
        return "AdvancedConfigSection{" +
                "googleAPIToken='" + googleAPIToken + '\'' +
                ", updateInfoUrl='" + updateInfoUrl + '\'' +
                ", useNodeServer=" + useNodeServer +
                ", nodesInfo=" + nodesInfo +
                ", logLevel='" + logLevel + '\'' +
                ", sentryDsn='" + sentryDsn + '\'' +
                '}';
    }
}
