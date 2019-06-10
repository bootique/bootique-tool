package io.bootique.tools.shell.content;

import io.bootique.command.CommandOutcome;

public class MavenModuleHandler extends ContentHandler {


    @Override
    public CommandOutcome handle(String name) {
        // validation:
        //      1. target dir doesn't exist
        //      2. current dir contains pom.xml
        //      3. pom.xml is with modules section? -> warning

        // processing:
        //      1. create target dir with proper module content (almost same as app creation, except for module naming)
        //      2. backup pom.xml
        //      3. alter pom.xml, add module section
        //      4. in case of exception, rollback pom.xml, in case of success delete it

        return CommandOutcome.failed(-1, "Not yet implemented");
    }
}

