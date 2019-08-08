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

package dev.pandasoft.simplejuke.util.update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VersionInfo {
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public String version;
    public String title;
    public List<String> description;
    public List<String> knownbugs;
    public String updatedate;
    public String download;
    public VersionType level;

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getKnownbugs() {
        return knownbugs;
    }

    public String getUpdateDateRaw() {
        return updatedate;
    }

    public Date getUpdateDate() throws ParseException {
        return FORMAT.parse(updatedate);
    }

    public String getDownload() {
        return download;
    }

    public VersionType getLevel() {
        return level;
    }
}
