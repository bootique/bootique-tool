package io.bootique.tools.shell.module;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import io.bootique.ModuleExtender;
import io.bootique.tools.shell.artifact.ArtifactHandler;

public class BQShellModuleExtender extends ModuleExtender<BQShellModuleExtender> {

    private MapBinder<String, ArtifactHandler> artifactHandlers;

    public BQShellModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public BQShellModuleExtender initAllExtensions() {
        contributeArtifactHandlers();
        return this;
    }

    public BQShellModuleExtender addHandler(String name, Class<? extends ArtifactHandler> handler) {
        contributeArtifactHandlers().addBinding(name).to(handler);
        return this;
    }

    public BQShellModuleExtender addHandler(String name, ArtifactHandler handler) {
        contributeArtifactHandlers().addBinding(name).toInstance(handler);
        return this;
    }

    protected MapBinder<String, ArtifactHandler> contributeArtifactHandlers() {
        // no synchronization. we don't care if it is created twice. It will still work with Guice.
        return artifactHandlers != null
                ? artifactHandlers
                : (artifactHandlers = newMap(String.class, ArtifactHandler.class));
    }
}
