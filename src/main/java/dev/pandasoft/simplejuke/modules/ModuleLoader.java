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

package dev.pandasoft.simplejuke.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.pandasoft.simplejuke.modules.exception.InvalidDescriptionException;
import dev.pandasoft.simplejuke.modules.exception.InvalidModuleException;
import dev.pandasoft.simplejuke.modules.exception.UnknownDependencyException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class ModuleLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final ModuleRegistry moduleRegistry;
    private final File dir;

    public ModuleLoader(ModuleRegistry moduleRegistry, String moduleDir) {
        this.moduleRegistry = moduleRegistry;
        this.dir = new File(moduleDir);
    }

    /**
     * モジュールフォルダに格納されているJARファイル一覧を取得します。<br>
     * フォルダが存在しないかからの場合は空のListを返します。
     *
     * @return モジュールフォルダに格納されているJARファイル一覧
     */
    public List<File> searchModules() {
        if (dir.exists())
            return Arrays.stream(dir.listFiles()).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList());
        else
            dir.mkdirs();
        return new ArrayList<>();
    }

    /**
     * 指定したファイルをモジュールとしてロードします。
     *
     * @param file ロードするモジュールファイル
     * @return ロードされたモジュール
     * @throws InvalidModuleException     モジュールの形式が正しくない場合にスローされます。
     * @throws UnknownDependencyException 指定されている依存関係が解決できない場合にスローされます。
     */
    public BotModule loadModule(File file) throws InvalidModuleException, UnknownDependencyException {
        if (!file.exists())
            throw new InvalidModuleException(file.getPath() + "は存在しません！");

        ModuleDescription description;
        try {
            description = loadModuleDescription(file);
        } catch (InvalidDescriptionException e) {
            throw new InvalidModuleException(e);
        }

        File parent = file.getParentFile();
        File dataFolder = new File(parent, description.getName());

        if (!dataFolder.exists())
            dataFolder.mkdirs();

        if (description.getDependency() != null && !description.getDependency().isEmpty()) {
            for (String dep : description.getDependency()) {
                if (moduleRegistry.getModule(dep) == null)
                    throw new UnknownDependencyException("依存関係が解決できませんでした。");
            }
        }

        ModuleClassLoader classLoader;
        try {
            classLoader = new ModuleClassLoader(file, description, getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new InvalidModuleException(e);
        }

        return classLoader.getModule();
    }

    /**
     * モジュールに登録されている詳細情報を読み込みます。
     *
     * @param file 読み込むモジュールファイル
     * @return 登録されている詳細情報
     * @throws InvalidModuleException      モジュールの形式が正しくない場合にスローされます。
     * @throws InvalidDescriptionException 指定されている依存関係が解決できない場合にスローされます。
     */
    public ModuleDescription loadModuleDescription(File file) throws InvalidModuleException, InvalidDescriptionException {
        if (!file.exists())
            throw new InvalidModuleException(file.getPath() + "は存在しません！");

        InputStream inputStream = null;

        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("module.yaml");

            if (entry == null)
                throw new InvalidDescriptionException("module.yamlが見つかりませんでした。");

            inputStream = jar.getInputStream(entry);
            return mapper.readValue(inputStream, ModuleDescription.class);
        } catch (IOException e) {
            throw new InvalidModuleException("モジュールをロードできませんでした。", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
