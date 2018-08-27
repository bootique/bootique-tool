import io.bootique.BQModuleProvider;
import io.bootique.tools.template.module.LiveTemplateModuleProvider;

module io.bootique.tools {
    requires bootique;
    requires java.base;
    requires java.xml;
    requires guice.over.bootique.di;

    provides BQModuleProvider with LiveTemplateModuleProvider;
}