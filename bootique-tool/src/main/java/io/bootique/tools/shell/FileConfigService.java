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

    private Shell shell;

    private final Path configFile;

    private final Map<ConfigParameter<?>, Object> storage;

    @Inject
    public FileConfigService(Shell shell, @ConfigDir Path configDirectory) {
        this.shell = shell;
        this.configFile = configDirectory.resolve("bq.config");
        this.storage = new HashMap<>();
        if(Files.exists(configFile) && Files.isReadable(configFile)) {
            try {
                Files.readAllLines(configFile).forEach(this::parseLine);
            } catch (IOException ex) {
                shell.println("Unable to read config file");
                shell.println(ex);
            }
        }
    }

    private void parseLine(String line) {
        String[] values = line.split("=");
        ConfigParameter<?> parameter = paramByName(values[0]);
        storage.put(parameter, parameter.valueFromString(values[1]));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String createLine(Map.Entry<ConfigParameter<?>, ?> entry) {
        ConfigParameter parameter = entry.getKey();
        return parameter.getName() + '=' + parameter.valueToString(entry.getValue());
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
        storage.put(param, value);
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
        return (T)storage.getOrDefault(param, param.getDefaultValue());
    }

}
