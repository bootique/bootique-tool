package io.bootique.tools.template.source;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class SourceTemplateFilter implements SourceFilter {

    final Pattern pattern;

    public SourceTemplateFilter(String template) {
        this.pattern = Pattern.compile(templateToRegexp(template));
    }

    static String templateToRegexp(String template) {
        // any file - "*"
        template = template.replaceAll("([^*]|^)\\*([^*]|$)", "$1[^/]+$2");
        // escape dots "."
        template = template.replaceAll("\\.", "\\\\.");
        // replace "?" with "."
        template = template.replaceAll("\\?", ".");
        // any folder - "**/" replace with "(.*/)?"
        template = template.replaceAll("\\*{2}/", "(.*/)?");

        // align file separator with current platform we run on
        if(File.separator.equals("\\")) {
            template = template.replaceAll("/", "\\\\");
        } else {
            template = template.replaceAll("/", File.separator);
        }

        // add end of patter guard to be able to differentiate "*.ext" and ".extension"
        return template + "$";
    }

    @Override
    public boolean test(Path path) {
        return pattern.matcher(path.toString()).matches();
    }
}
