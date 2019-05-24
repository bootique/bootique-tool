package io.bootique.tools.template.module;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class LiveTemplateModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new LiveTemplateModule();
    }

    @Override
    public Map<String, Type> configs() {
        return Collections.singletonMap("templates", TemplateServiceFactory.class);
    }
}
