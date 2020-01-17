/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package io.bootique.tools.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class FileConfigService implements ConfigService {

    @Inject
    private Shell shell;

    private final Path configFile;

    private final Map<String, Object> storage;

    @Inject
    public FileConfigService(@ConfigDir Path configDirectory) {
        configFile = configDirectory.resolve("bq.config");
        storage = new HashMap<>();
        if(Files.exists(configFile)) {
            if(Files.isReadable(configFile)) {
                try {
                    Files.readAllLines(configFile)
                            .forEach(this::parseLine);
                } catch (IOException ex) {
                    shell.println("Unable to read config file");
                    shell.println(ex);
                }
            }
        }
    }

    private void parseLine(String line) {
        String[] values = line.split("=");
        storage.put(values[0], values[1]);
    }

    private String createLine(Map.Entry<String, ?> entry) {
        return entry.getKey() + '=' + entry.getValue();
    }

    @Override
    public ConfigParameter<?> paramByName(String name) {
        switch (name.toLowerCase()) {
            case "toolchain":
                return TOOLCHAIN;
            case "java-version":
                return JAVA_VERSION;
            case "bq-version":
                return BQ_VERSION;
            case "group-id":
                return GROUP_ID;
            case "packaging":
                return PACKAGING;
        }
        return null;
    }

    @Override
    public <T> void set(ConfigParameter<T> param, T value) {
        String strValue = param.valueToString(value);
        storage.put(param.getName(), strValue);
        List<String> lines = storage.entrySet().stream()
                .map(this::createLine)
                .collect(Collectors.toList());
        try {
            Files.write(configFile, lines,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            shell.println("Unable to write config file");
            shell.println(ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(ConfigParameter<T> param) {
        return (T)storage.getOrDefault(param.getName(), param.getDefaultValue());
    }

}
