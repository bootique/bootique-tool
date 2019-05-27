package io.bootique.tools.shell.module;

import java.util.Collection;
import java.util.Collections;

import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.BQModuleProvider;

public class BqShellModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new BqShellModule();
    }

    @Override
    public Collection<Class<? extends Module>> overrides() {
        return Collections.singleton(BQCoreModule.class);
    }
}
