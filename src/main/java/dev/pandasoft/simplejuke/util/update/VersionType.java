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

public enum VersionType {
    INFO(999),
    STABLE(3),
    BETA(2),
    ALPHA(1),
    NIGHTLY(0);

    private final int level;

    VersionType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
