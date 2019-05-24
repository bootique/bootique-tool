package io.bootique.tools.template.module;

import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.command.CommandManager;
import io.bootique.config.ConfigurationFactory;
import io.bootique.tools.template.DefaultPropertyService;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.TemplateService;
import io.bootique.tools.template.processor.JavaPackageProcessor;
import io.bootique.tools.template.processor.MavenProcessor;
import io.bootique.tools.template.processor.TemplateProcessor;
import io.bootique.tools.template.shell.ShellCommand;
import io.bootique.type.TypeRef;

public class LiveTemplateModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {
        binder.bind(PropertyService.class).to(DefaultPropertyService.class).in(Singleton.class);

        BQCoreModule.extend(binder)
                .addCommand(NewProjectCommand.class)
                .addCommand(ShellCommand.class)
                .setDefaultCommand(ShellCommand.class);

        contributeProcessor(binder, "javaPackage", JavaPackageProcessor.class);
        contributeProcessor(binder, "maven", MavenProcessor.class);
    }

    public static void contributeProcessor(Binder binder, String name, TemplateProcessor processor) {
        MapBinder.newMapBinder(binder, String.class, TemplateProcessor.class)
                .addBinding(name).toInstance(processor);
    }

    public static void contributeProcessor(Binder binder, String name, Class<? extends TemplateProcessor> processor) {
        MapBinder.newMapBinder(binder, String.class, TemplateProcessor.class)
                .addBinding(name).to(processor);
    }

    @Singleton
    @Provides
    public TemplateService createTemplateService(ConfigurationFactory configurationFactory, Map<String, TemplateProcessor> processorMap) {
        return configurationFactory
                .config(TemplateServiceFactory.class, configPrefix)
                .createTemplateService(processorMap);
    }

    @Singleton
    @Provides
    public PropertyService createPropertyService(ConfigurationFactory configurationFactory) {
        Map<String, String> props = configurationFactory.config(new TypeRef<Map<String, String>>() {}, "properties");
        PropertyService propertyService = new DefaultPropertyService();
        props.forEach(propertyService::setProperty);
        return propertyService;
    }

    @Override
    protected String defaultConfigPrefix() {
        return "templates";
    }
}
