package io.bootique.tools.template.module;

import io.bootique.BQModuleProvider;

public class LiveTemplateModuleProvider implements BQModuleProvider {

    @Override
    public com.google.inject.Module module() {
        return new LiveTemplateModule();
    }

}
