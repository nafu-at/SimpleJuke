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
import dev.pandasoft.simplejuke.modules.exception.InvalidModuleException;
import dev.pandasoft.simplejuke.modules.meta.ModuleDescription;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModuleClassLoader extends URLClassLoader {
    private final BotBuilder builder;
    private final ModuleDescription description;
    private final BotModule module;

    public ModuleClassLoader(File file, ModuleDescription description, ClassLoader parent, BotBuilder builder) throws MalformedURLException, InvalidModuleException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.builder = builder;
        this.description = description;

        Class<?> jarClass;
        try {
            jarClass = Class.forName(description.getMain(), true, this);
        } catch (ClassNotFoundException e) {
            throw new InvalidModuleException("メインクラスが見つかりませんでした。");
        }
        Class<? extends BotModule> moduleClass;
        try {
            moduleClass = jarClass.asSubclass(BotModule.class);
        } catch (ClassCastException e) {
            throw new InvalidModuleException("メインクラスにBotModuleクラスがエクステンドされていません。");
        }


        try {
            module = moduleClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidModuleException("モジュールの形式が正しくありません。");
        } catch (IllegalAccessException e) {
            throw new InvalidModuleException("コンストラクタがパブリックではありません。");
        }
    }

    public BotModule getModule() {
        return module;
    }

    public void initialize(BotModule module) {
        module.init(description, builder);
    }
}
