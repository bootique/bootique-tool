package io.bootique.tools.template.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.template.TemplateService;

public class NewProjectCommand implements Command {

    @Inject
    Provider<TemplateService> templateService;

    @Override
    public CommandOutcome run(Cli cli) {
        templateService.get().process();
        return CommandOutcome.succeeded();
    }

}
