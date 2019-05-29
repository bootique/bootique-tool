package io.bootique.tools.shell.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.BootiqueException;
import io.bootique.annotation.DefaultCommand;
import io.bootique.command.Command;
import io.bootique.command.CommandManager;
import io.bootique.command.CommandManagerBuilder;
import io.bootique.tools.shell.JlineShell;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.artifact.GradleProjectHandler;
import io.bootique.tools.shell.artifact.NewModuleHandler;
import io.bootique.tools.shell.artifact.MavenProjectHandler;
import io.bootique.tools.shell.command.CommandLineParser;
import io.bootique.tools.shell.command.DefaultCommandLineParser;
import io.bootique.tools.shell.command.ErrorCommand;
import io.bootique.tools.shell.command.ExitCommand;
import io.bootique.tools.shell.command.HelpCommand;
import io.bootique.tools.shell.command.NewCommand;
import io.bootique.tools.shell.command.RunCommand;
import io.bootique.tools.shell.command.ShellCommand;
import io.bootique.tools.shell.command.StartShellCommand;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import static org.jline.builtins.Completers.*;
import static org.jline.builtins.Completers.TreeCompleter.*;

public class BQShellModule implements Module {

    public static BQShellModuleExtender extend(Binder binder) {
        return new BQShellModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        // all commands
        BQCoreModule.extend(binder)
                .addCommand(StartShellCommand.class)
                .addCommand(NewCommand.class)
                .addCommand(RunCommand.class)
                .addCommand(ErrorCommand.class)
                .addCommand(ExitCommand.class)
                .setDefaultCommand(StartShellCommand.class);

        // new content handlers
        extend(binder)
                .addHandler("maven-project", MavenProjectHandler.class)
                .addHandler("gradle-project", GradleProjectHandler.class)
                .addHandler("module", NewModuleHandler.class);

        binder.bind(CommandLineParser.class).to(DefaultCommandLineParser.class).in(Singleton.class);
        binder.bind(Shell.class).to(JlineShell.class).in(Singleton.class);
    }

    /**
     * Override default CommandManager to use own help command
     */
    @Provides
    @Singleton
    CommandManager provideCommandManager(Set<Command> commands,
                                         Injector injector,
                                         @DefaultCommand Command defaultCommand) {
        return new CommandManagerBuilder(commands)
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
        Object[] nodes = new Object[shellCommands.size()];
        AtomicInteger counter = new AtomicInteger();
        shellCommands.forEach((name, cmd) -> nodes[counter.getAndIncrement()] = name);

        FileNameCompleter dirNameCompleter = new FileNameCompleter() {
            protected boolean accept(Path path) {
                try {
                    return !Files.isHidden(path) && Files.isDirectory(path);
                } catch (IOException e) {
                    return false;
                }
            }
        };

        //TODO: create this from metadata
        return new TreeCompleter(
                node("help", node(nodes)),
                node("new", node("maven-project", "gradle-project", "module")),
                node("run", node(dirNameCompleter)),
                node("info", node(dirNameCompleter)),
                node("exit")
        );
    }

    @Provides
    @Singleton
    LineReader createLineReader(Terminal terminal, Completer completer) {
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
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
                    result.put(name, (ShellCommand)command);
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
