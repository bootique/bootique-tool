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

package io.bootique.tools.shell.module;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import io.bootique.BQCoreModule;
import io.bootique.BootiqueException;
import io.bootique.annotation.DefaultCommand;
import io.bootique.command.Command;
import io.bootique.command.CommandManager;
import io.bootique.command.CommandManagerBuilder;
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Injector;
import io.bootique.di.Provides;
import io.bootique.tools.shell.ConfigDir;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.FileConfigService;
import io.bootique.tools.shell.JlineShell;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.command.terminal.CdCommand;
import io.bootique.tools.shell.command.CommandLineParser;
import io.bootique.tools.shell.command.ConfigCommand;
import io.bootique.tools.shell.command.DefaultCommandLineParser;
import io.bootique.tools.shell.command.ErrorCommand;
import io.bootique.tools.shell.command.ExitCommand;
import io.bootique.tools.shell.command.HelpCommand;
import io.bootique.tools.shell.command.NewCommand;
import io.bootique.tools.shell.command.terminal.LsCommand;
import io.bootique.tools.shell.command.terminal.PwdCommand;
import io.bootique.tools.shell.command.ShellCommand;
import io.bootique.tools.shell.command.StartShellCommand;
import io.bootique.tools.shell.content.GradleAppHandler;
import io.bootique.tools.shell.content.GradleMultimoduleHandler;
import io.bootique.tools.shell.content.MavenAppHandler;
import io.bootique.tools.shell.content.MavenModuleHandler;
import io.bootique.tools.shell.content.GradleModuleHandler;
import io.bootique.tools.shell.content.MavenMultimoduleHandler;
import org.jline.reader.Completer;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import static org.jline.builtins.Completers.TreeCompleter;
import static org.jline.builtins.Completers.TreeCompleter.Node;

public class BQShellModule implements BQModule {

    public static BQShellModuleExtender extend(Binder binder) {
        return new BQShellModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        // all commands
        BQCoreModule.extend(binder)
                // bootique CLI
                .addCommand(StartShellCommand.class)
                .addCommand(NewCommand.class)
                .addCommand(ErrorCommand.class)
                .addCommand(ExitCommand.class)
                .addCommand(ConfigCommand.class)
                // terminal-like commands
                .addCommand(CdCommand.class)
                .addCommand(PwdCommand.class)
                .addCommand(LsCommand.class)
                .setDefaultCommand(StartShellCommand.class);

        // new content handlers
        extend(binder)
                .addHandler("maven-app", MavenAppHandler.class)
                .addHandler("gradle-app", GradleAppHandler.class)
                .addHandler("maven-module", MavenModuleHandler.class)
                .addHandler("gradle-module", GradleModuleHandler.class)
                .addHandler("maven-multimodule", MavenMultimoduleHandler.class)
                .addHandler("gradle-multimodule", GradleMultimoduleHandler.class);

        binder.bind(CommandLineParser.class).to(DefaultCommandLineParser.class).inSingletonScope();
        binder.bind(Shell.class).to(JlineShell.class).inSingletonScope();
        binder.bind(ConfigService.class).to(FileConfigService.class).inSingletonScope();
        binder.bind(PathCompleter.class).inSingletonScope();
    }

    /**
     * Override default CommandManager to use own help command
     */
    @Provides
    @Singleton
    CommandManager provideCommandManager(Set<Command> commands,
                                         Injector injector,
                                         @DefaultCommand Command defaultCommand) {
        return new CommandManagerBuilder<>(commands)
                .defaultCommand(Optional.of(defaultCommand))
                .helpCommand(injector.getInstance(HelpCommand.class))
                .build();
    }

    @Provides
    @Singleton
    Terminal createTerminal() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .jansi(true)
                .build();
        terminal.echo(false);
        terminal.enterRawMode();
        return terminal;
    }

    @Provides
    @Singleton
    Completer createCompleter(Map<String, ShellCommand> shellCommands) {
        Node[] cmdNodes = shellCommands.values().stream()
                .map(ShellCommand::getCompleter)
                .filter(Objects::nonNull)
                .toArray(Node[]::new);
        return new TreeCompleter(cmdNodes);
    }

    @Provides
    @Singleton
    History createHistory() {
        DefaultHistory history = new DefaultHistory();
        Thread historyHook = new Thread(() -> {
            try {
                history.save();
            } catch (IOException ignore) {
            }
        });
        Runtime.getRuntime().addShutdownHook(historyHook);
        return history;
    }

    @Provides
    @Singleton
    @ConfigDir
    Path getConfigDirectory() {
        return Paths.get(System.getProperty("user.home"), ".bq");
    }

    @Provides
    @Singleton
    LineReader createLineReader(Terminal terminal, Completer completer, History history, @ConfigDir Path configDirectory) {
        Path historyPath = configDirectory.resolve("bq.history");
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .history(history)
                .variable(LineReader.HISTORY_FILE, historyPath)
                .option(LineReader.Option.AUTO_REMOVE_SLASH, true)
                .build();
    }

    /**
     * Collect only shell commands from all commands registered in app
     * @see ShellCommand
     */
    @Provides
    @Singleton
    Map<String, ShellCommand> getShellCommands(CommandManager commandManager) {
        Map<String, ShellCommand> result = new HashMap<>();
        commandManager.getAllCommands().forEach((name, cmd) -> {
            if(!cmd.isHidden()) {
                Command command = cmd.getCommand();
                if(command instanceof ShellCommand) {
                    ShellCommand shellCommand = (ShellCommand) command;
                    result.put(name, shellCommand);
                    for(String alias : shellCommand.aliases()) {
                        result.put(alias, shellCommand);
                    }
                }
            }
        });
        return result;
    }

    /**
     * Fallback shell command to handle wrong input
     */
    @Provides
    @Singleton
    ShellCommand defaultShellCommand(CommandManager commandManager) {
        ShellCommand[] command = new ShellCommand[1];
        commandManager.getAllCommands().forEach((n, cmd) -> {
            if(cmd.isHidden()) {
                Command nextCandidate = cmd.getCommand();
                if(nextCandidate instanceof ShellCommand) {
                    if(command[0] != null) {
                        throw new BootiqueException(ShellCommand.TERMINATING_EXIT_CODE
                                , "Multiple default commands configured for shell: "
                                + command[0].getMetadata().getName() + ", "
                                + nextCandidate.getMetadata().getName());
                    }
                    command[0] = (ShellCommand) nextCandidate;
                }
            }
        });

        if(command[0] == null) {
            throw new BootiqueException(ShellCommand.TERMINATING_EXIT_CODE
                    , "No default command configured for shell.");
        }
        return command[0];
    }
}
