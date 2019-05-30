package io.bootique.tools.shell.template.processor;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;


public class GradleProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        // replace following entries:
        // group 'org.example'
        // version '1.0-SNAPSHOT'
        // mainClassName = "example.Application"
        // rootProject.name = 'example'

        return template;
    }
}
