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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.pandasoft.simplejuke.Main;
import dev.pandasoft.simplejuke.util.update.UpdateInfo;
import dev.pandasoft.simplejuke.util.update.VersionInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UpdateInfoReader {
    private static final String VERSION = Main.class.getPackage().getImplementationVersion();
    private static final OkHttpClient client = new OkHttpClient();

    private final String infoUrl;
    private UpdateInfo updateInfo;

    public UpdateInfoReader(String serverUrl) {
        infoUrl = serverUrl + "/SimpleJukeUpdate.yaml";
    }

    public static String getNowVersion() {
        return VERSION;
    }

    protected void loadUpdateInfo() {
        Request request = new Request.Builder().url(infoUrl).build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            updateInfo = mapper.readValue(result, UpdateInfo.class);
        } catch (IOException e) {
            log.error("更新情報の取得中にエラーが発生しました。", e);
        }
    }

    public boolean checkUpdate() {
        return checkUpdate(3);
    }

    public boolean checkUpdate(int level) {
        VersionInfo latestVersion = null;
        VersionInfo nowVersion = null;
        if (level == 999) {
            level = 3;
        }

        if (updateInfo == null)
            return false;

        for (VersionInfo versionInfo : updateInfo.getVersions()) {
            if (versionInfo.getLevel().getLevel() >= level)
                latestVersion = versionInfo;
            if (versionInfo.getLevel().equals(VERSION))
                nowVersion = versionInfo;
        }

        if (latestVersion != null && nowVersion != null) {
            try {
                return latestVersion.getUpdateDate().after(nowVersion.getUpdateDate());
            } catch (ParseException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<VersionInfo> getUpdateInfo(int level) {
        List<VersionInfo> versionInfoList = new ArrayList();
        updateInfo.getVersions().forEach(versionInfo -> {
            if (versionInfo.getLevel().getLevel() >= level)
                versionInfoList.add(versionInfo);
        });
        return versionInfoList;
    }
}

