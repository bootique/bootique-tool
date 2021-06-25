package io.bootique.tools.shell.config;

import io.bootique.tools.shell.template.*;

public class LoaderFactory {
    public static TemplateLoader getLoaderWithType(LoaderType loaderType) {
        switch (loaderType) {
            case EMPTY:
                return new EmptyTemplateLoader();
            case TEMPLATE_RESOURCE:
                return new TemplateResourceLoader();
            case BINARY_FILE:
                return new BinaryFileLoader();
            case BINARY_RESOURCE:
                return new BinaryResourceLoader();
            default:
                throw new IllegalArgumentException("Unrecognizable loader type: " + loaderType);
        }
    }
}
