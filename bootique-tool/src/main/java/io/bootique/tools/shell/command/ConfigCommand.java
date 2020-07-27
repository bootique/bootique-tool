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

package io.bootique.tools.shell.command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.*;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class ConfigCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Provider<ConfigService> configServiceProvider;

    @Inject
    private Shell shell;

    private static final Map<ConfigParameter<?>, String> SUPPORTED_PARAMS;
    static {
        SUPPORTED_PARAMS = new TreeMap<>(Comparator.comparing(ConfigParameter::getName));
        SUPPORTED_PARAMS.put(ConfigService.TOOLCHAIN,    "Default toolchain to use. Can be either Maven or Gradle.");
        SUPPORTED_PARAMS.put(ConfigService.JAVA_VERSION, "Java version to use.");
        SUPPORTED_PARAMS.put(ConfigService.BQ_VERSION,   "Bootique version to use.");
        SUPPORTED_PARAMS.put(ConfigService.GROUP_ID,     "Default artifact group id to use.");
        SUPPORTED_PARAMS.put(ConfigService.PACKAGING,    "App packaging method. Can be either Shade or Assembly.");
        SUPPORTED_PARAMS.put(ConfigService.CONTAINER,    "Container for app. Can be either Docker or Jib.");
    }

    public ConfigCommand() {
        super(CommandMetadata.builder("config")
                .description("Read or set global config. Available parameters: "
                        + SUPPORTED_PARAMS.keySet().stream().map(ConfigParameter::getName).collect(Collectors.joining(", ")))
                .addOption(OptionMetadata.builder("param")
                        .description("parameter name, optional")
                        .valueOptional()
                        .build())
                .addOption(OptionMetadata.builder("value")
                        .description("parameter value, optional")
                        .valueOptional()
                        .build())
                .build());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public CommandOutcome run(Cli cli) {

        List<String> args = cli.standaloneArguments();
        String param = null;
        String value = null;
        switch (args.size()) {
            case 2:
                value = args.get(1);
            case 1:
                param = args.get(0);
        }

        ConfigService configService = this.configServiceProvider.get();
        ConfigParameter<?> parameter = param == null
                ? null
                : configService.paramByName(param);

        if(parameter != null) {
            if(value != null) {
                // set
                CommandOutcome outcome = validate(parameter, value);
                if(!outcome.isSuccess()) {
                    return outcome;
                }
                configService.set((ConfigParameter)parameter, parameter.valueFromString(value));
            } else {
                // get
                value = configService.get(parameter).toString();
                if(value == null) {
                    shell.println("No value is set for @|bold " + param + "|@");
                } else {
                    shell.println(param + " = " + value);
                }
            }
        } else {
            // get all
            shell.println("Available configuration options:");
            SUPPORTED_PARAMS.forEach(this::formatConfigParameter);
        }

        return CommandOutcome.succeeded();
    }

    private CommandOutcome validate(ConfigParameter<?> param, String value) {
        if(!SUPPORTED_PARAMS.containsKey(param)) {
            String possibleParameters = SUPPORTED_PARAMS.keySet().stream()
                    .map(ConfigParameter::getName)
                    .collect(Collectors.joining(", "));
            return CommandOutcome.failed(-1, "Unsupported option @|bold " + param
                    + "|@. Available parameters: " + possibleParameters);
        }

        if(ConfigService.TOOLCHAIN.equals(param)) {
            Toolchain toolchain = Toolchain.byName(value);
            if(toolchain == null) {
                return CommandOutcome.failed(-1, "Unsupported toolchain @|bold " + value
                        + "|@. Supported: " + Arrays.stream(Toolchain.values())
                                                .map(s -> s.name().toLowerCase())
                                                .collect(Collectors.joining(", ")));

            }
        }

        if(ConfigService.PACKAGING.equals(param)) {
            Packaging packaging = Packaging.byName(value);
            if(packaging == null) {
                return CommandOutcome.failed(-1, "Unsupported packaging @|bold " + value
                        + "|@. Supported: " + Arrays.stream(Packaging.values())
                        .map(s -> s.name().toLowerCase())
                        .collect(Collectors.joining(", ")));
            }
        }

        if(ConfigService.CONTAINER.equals(param)) {
            Container container = Container.byName(value);
            if(container == null) {
                return CommandOutcome.failed(-1, "Unsupported packaging @|bold " + value
                        + "|@. Supported: " + Arrays.stream(Container.values())
                        .map(s -> s.name().toLowerCase())
                        .collect(Collectors.joining(", ")));
            }
        }

        return CommandOutcome.succeeded();
    }

    private void formatConfigParameter(ConfigParameter<?> param, String description) {
        ConfigService configService = this.configServiceProvider.get();
        Object value = configService.get(param);
        if(value == null) {
            value = "none";
        }

        shell.println(
                "  @|green " + Formatter.alignByColumns(param.getName()) + "|@"
                + description
                + " Current value: @|bold " + value + " |@"
        );
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        return node("config",
                node(ConfigService.BQ_VERSION.getName()),
                node(ConfigService.GROUP_ID.getName()),
                node(ConfigService.JAVA_VERSION.getName()),
                node(ConfigService.PACKAGING.getName(),
                        node(Packaging.ASSEMBLY.name().toLowerCase()), node(Packaging.SHADE.name().toLowerCase())),
                node(ConfigService.TOOLCHAIN.getName(),
                        node(Toolchain.MAVEN.name().toLowerCase()), node(Toolchain.GRADLE.name().toLowerCase())),
                node(ConfigService.CONTAINER.getName(),
                        node(Container.DOCKER.name().toLowerCase()), node(Container.JIB.name().toLowerCase()))
        );
    }
}
