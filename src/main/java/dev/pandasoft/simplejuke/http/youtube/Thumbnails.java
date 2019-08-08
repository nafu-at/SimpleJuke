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

package dev.pandasoft.simplejuke.http.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Thumbnails {
    private ThumbnailDefault thumbnailsDefault;
    private ThumbnailDefault medium;
    private ThumbnailDefault high;
    private ThumbnailDefault standard;
    private ThumbnailDefault maxres;

    @JsonProperty("default")
    public ThumbnailDefault getThumbnailsDefault() {
        return thumbnailsDefault;
    }

    @JsonProperty("medium")
    public ThumbnailDefault getMedium() {
        return medium;
    }

    @JsonProperty("high")
    public ThumbnailDefault getHigh() {
        return high;
    }

    @JsonProperty("standard")
    public ThumbnailDefault getStandard() {
        return standard;
    }

    @JsonProperty("maxres")
    public ThumbnailDefault getMaxres() {
        return maxres;
    }


    public static class ThumbnailDefault {
        private String url;
        private long width;
        private long height;

        @JsonProperty("url")
        public String getURL() {
            return url;
        }

        @JsonProperty("width")
        public long getWidth() {
            return width;
        }

        @JsonProperty("height")
        public long getHeight() {
            return height;
        }
    }
}
