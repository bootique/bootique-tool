package io.bootique.tools.shell.template.processor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateException;

public class MustacheTemplateProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template.getContent()), "template");
        StringWriter writer = new StringWriter();
        try {
            mustache.execute(writer, properties.asMap()).flush();
        } catch (IOException e) {
            throw new TemplateException("Unable to render template", e);
        }
        return template.withContent(writer.toString());
    }

}
