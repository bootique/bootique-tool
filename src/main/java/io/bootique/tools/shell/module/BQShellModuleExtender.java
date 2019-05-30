package io.bootique.tools.shell.module;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import io.bootique.ModuleExtender;
import io.bootique.tools.shell.content.ContentHandler;

public class BQShellModuleExtender extends ModuleExtender<BQShellModuleExtender> {

    private MapBinder<String, ContentHandler> contentHandlers;

    public BQShellModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public BQShellModuleExtender initAllExtensions() {
        contributeArtifactHandlers();
        return this;
    }

    public BQShellModuleExtender addHandler(String name, Class<? extends ContentHandler> handler) {
        contributeArtifactHandlers().addBinding(name).to(handler);
        return this;
    }

    public BQShellModuleExtender addHandler(String name, ContentHandler handler) {
        contributeArtifactHandlers().addBinding(name).toInstance(handler);
        return this;
    }

    protected MapBinder<String, ContentHandler> contributeArtifactHandlers() {
        // no synchronization. we don't care if it is created twice. It will still work with Guice.
        return contentHandlers != null
                ? contentHandlers
                : (contentHandlers = newMap(String.class, ContentHandler.class));
    }
}
