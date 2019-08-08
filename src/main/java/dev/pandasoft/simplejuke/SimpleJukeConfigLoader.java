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

package dev.pandasoft.simplejuke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.pandasoft.simplejuke.config.SimpleJukeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;

@Slf4j
public class SimpleJukeConfigLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final File file = new File("SimpleJuke.yaml");

    public SimpleJukeConfigLoader() {
        if (!file.exists()) {
            try (InputStream original = ClassLoader.getSystemResourceAsStream("SimpleJuke.yaml")) {
                Files.copy(original, file.toPath());
            } catch (IOException e) {
                log.error("設定ファイルの生成に失敗しました。", e);
                System.exit(1);
            }
            log.info("設定ファイルを生成しました。設定を変更してください。");
            System.exit(0);
        }
    }

    public SimpleJukeConfig reloadConfig() {
        try {
            return mapper.readValue(new FileInputStream(file), SimpleJukeConfig.class);
        } catch (FileNotFoundException e) {
            log.error("設定ファイルが存在しません！");
            return null;
        } catch (IOException e) {
            log.error("設定ファイルの読み込みに失敗しました。", e);
            return null;
        }
    }

    public boolean checkUpdate() {
        try (InputStream original = ClassLoader.getSystemResourceAsStream("SimpleJuke.yaml")) {
            SimpleJukeConfig local = mapper.readValue(new FileInputStream(file), SimpleJukeConfig.class);
            SimpleJukeConfig res = mapper.readValue(original, SimpleJukeConfig.class);
            log.debug("Local Config: {}, Built-in: {}", local.getConfigVersion(), res.getConfigVersion());
            if (res.getConfigVersion().equals(local.getConfigVersion()))
                return true;
        } catch (FileNotFoundException e) {
            System.exit(1);
            log.error("設定ファイルが存在しません！プログラムを再起動して下さい。");
        } catch (IOException e) {
            log.error("設定ファイルの読み込みに失敗しました。", e);
        }
        return false;
    }
}
