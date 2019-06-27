package io.bootique.tools.shell.module;

import java.util.Collection;
import java.util.Collections;

import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.BQModuleProvider;

public class BQShellModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
            return new BQWinShellModule();
        } else {
            return new BQShellModule();
        }
    }

    @Override
    public Collection<Class<? extends Module>> overrides() {
        return Collections.singleton(BQCoreModule.class);
    }
}
