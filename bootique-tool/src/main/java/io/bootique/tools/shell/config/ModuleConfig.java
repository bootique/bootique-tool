package io.bootique.tools.shell.config;

import io.bootique.tools.shell.template.TemplatePipeline;

import java.util.List;

public class ModuleConfig {
    private final List<TemplatePipeline.Builder> templatePipelineBuilders;
    private final String modulePrototypePath;

    public ModuleConfig(List<TemplatePipeline.Builder> templatePipelineBuilders, String modulePrototypePath) {
        this.templatePipelineBuilders = templatePipelineBuilders;
        this.modulePrototypePath = modulePrototypePath;
    }

    public List<TemplatePipeline.Builder> getTemplatePipelineBuilders() {
        return templatePipelineBuilders;
    }

    public String getModulePrototypePath() {
        return modulePrototypePath;
    }
}
