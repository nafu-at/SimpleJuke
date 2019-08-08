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

import dev.pandasoft.simplejuke.BotBuilder;
import dev.pandasoft.simplejuke.modules.exception.InvalidDescriptionException;
import dev.pandasoft.simplejuke.modules.exception.InvalidModuleException;
import dev.pandasoft.simplejuke.modules.exception.UnknownDependencyException;
import dev.pandasoft.simplejuke.modules.meta.ModuleDescription;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class ModuleLoader {
    private static final File dir = new File("modules");
    private static final Yaml yaml = new Yaml();
    private final BotBuilder builder;

    public ModuleLoader(BotBuilder builder) {
        this.builder = builder;
    }

    public List<File> searchModules() {
        if (dir.exists())
            return new ArrayList<>(Arrays.asList(dir.listFiles()));
        else
            dir.mkdirs();
        return new ArrayList<>();
    }

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
                if (ModuleRegistry.getModule(dep) == null)
                    throw new UnknownDependencyException("依存関係が解決できませんでした。");
            }
        }

        ModuleClassLoader classLoader;
        try {
            classLoader = new ModuleClassLoader(file, description, getClass().getClassLoader(), builder);
        } catch (MalformedURLException e) {
            throw new InvalidModuleException(e);
        }

        return classLoader.getModule();
    }

    public ModuleDescription loadModuleDescription(File file) throws InvalidModuleException, InvalidDescriptionException {
        if (!file.exists())
            throw new InvalidModuleException(file.getPath() + "は存在しません！");

        InputStream inputStream = null;

        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("module.yaml");

            if (entry == null)
                throw new InvalidDescriptionException("module.yamlが見つかりませんでした。");

            inputStream = jar.getInputStream(entry);
            return yaml.loadAs(inputStream, ModuleDescription.class);
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
