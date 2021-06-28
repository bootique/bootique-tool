package io.bootique.tools.shell.config;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.tools.shell.template.TemplateLoader;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.TemplateSaver;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

import java.util.Collection;

@BQConfig
public class TemplatePipelineBuilderFactory {
    private final TemplatePipeline.Builder builder = TemplatePipeline.builder();

    private Integer permissions;
    private SaverType saverType;

    public TemplatePipelineBuilderFactory() {
    }

    @BQConfigProperty("Permissions like number, which can be used in chmod command")
    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    @BQConfigProperty("List of paths to files")
    public void setSources(Collection<String> sources) {
        for (String source : sources)
            builder.source(source);
    }

    @BQConfigProperty("Identifiers of processors (\"module_path\", \"java\",\"mustache\",\"parent_pom\",\"settings_gradle\")")
    public void setProcessors(Collection<String> processorQualifiers) {
        for (String processorQualifier : processorQualifiers) {
            try {
                ProcessorType processorType = ProcessorType.valueOf(processorQualifier.toUpperCase());
                TemplateProcessor processor = ProcessorsFactory.getProcessorWithType(processorType);
                builder.processor(processor);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Incorrect processor qualifier: " + processorQualifier);
            }
        }
    }

    @BQConfigProperty("Loader identifier (\"binary_file\",\"binary_resource\",\"empty\",\"template_resource\",\"external_resource\")")
    public void setLoader(String loaderName) {
        try {
            LoaderType loaderType = LoaderType.valueOf(loaderName.toUpperCase());
            TemplateLoader loader = LoaderFactory.getLoaderWithType(loaderType);
            builder.loader(loader);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Incorrect loader qualifier: " + loaderName);
        }
    }

    @BQConfigProperty("Saver identifier (\"binary\",\"safe_binary\",\"dir_only\",\"file\")")
    public void setSaver(String saverName) {
        try {
            saverType = SaverType.valueOf(saverName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Incorrect saver qualifier: " + saverName);
        }
    }

    public TemplatePipeline.Builder builder() {
        buildSaver();
        return builder;
    }

    private void buildSaver() {
        if (saverType != null) {
            SaverFactory saverFactory = new SaverFactory();
            saverFactory.setPermissions(permissions);
            TemplateSaver saver = saverFactory.getSaverWithType(saverType);
            builder.saver(saver);
        }
    }
}
