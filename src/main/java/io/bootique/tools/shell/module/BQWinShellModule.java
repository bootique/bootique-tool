package io.bootique.tools.shell.module;

import java.io.IOException;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;

public class BQWinShellModule extends BQShellModule {

    @Provides
    @Singleton
    Terminal createTerminal() throws IOException {
        return new DumbTerminal(System.in, System.out);
    }

}
