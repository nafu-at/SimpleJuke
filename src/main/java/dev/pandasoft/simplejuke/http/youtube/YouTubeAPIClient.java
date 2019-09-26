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

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pandasoft.simplejuke.Main;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class YouTubeAPIClient {
    public static final String YOUTUBE_VIDEO = "https://www.googleapis.com/youtube/v3/videos";
    public static final String YOUTUBE_CHANNEL = "https://www.googleapis.com/youtube/v3/channels";
    private static final String YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search";
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    public YouTubeSearchResults searchVideos(@NonNull String query) throws IOException {
        return searchVideos(query, null);
    }

    public YouTubeSearchResults searchVideos(@NonNull String query, String pageToken) throws IOException {
        if (!Main.getController().getConfig().getBasicConfig().getMusicSource().enableYoutube() ||
                StringUtils.isBlank(Main.getController().getConfig().getAdvancedConfig().getGoogleAPIToken()))
            return null;

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(YOUTUBE_SEARCH)).newBuilder();
        urlBuilder.addQueryParameter("key", Main.getController().getConfig().getAdvancedConfig().getGoogleAPIToken());
        urlBuilder.addQueryParameter("part", "snippet");
        urlBuilder.addQueryParameter("type", "video");
        urlBuilder.addQueryParameter("maxResults", "5");
        if (pageToken != null)
            urlBuilder.addQueryParameter("pageToken", pageToken);
        urlBuilder.addQueryParameter("q", query);

        Request request = new Request.Builder().url(urlBuilder.build()).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            log.debug("YouTube API Response Received: {}", response.toString());
            if (response.code() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body().string(), YouTubeSearchResults.class);
            }
            return null;
        }
    }

    public YouTubeObjects getYoutubeObjects(@NonNull String endpoint, @NonNull String id) throws IOException {
        if (!Main.getController().getConfig().getBasicConfig().getMusicSource().enableYoutube() ||
                Main.getController().getConfig().getAdvancedConfig().getGoogleAPIToken() == null)
            return null;

        HttpUrl.Builder urlBuilder = HttpUrl.parse(endpoint).newBuilder();
        urlBuilder.addQueryParameter("key", Main.getController().getConfig().getAdvancedConfig().getGoogleAPIToken());
        urlBuilder.addQueryParameter("part", "snippet");
        urlBuilder.addQueryParameter("hl", "ja");
        urlBuilder.addQueryParameter("id", id);

        Request request = new Request.Builder().url(urlBuilder.build()).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                log.debug("YouTube API Response Received: {}", response.toString());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body().string(), YouTubeObjects.class);
            }
            return null;
        }
    }
}
