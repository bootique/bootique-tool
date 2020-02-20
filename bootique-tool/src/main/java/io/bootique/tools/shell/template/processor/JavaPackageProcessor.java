/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package io.bootique.tools.shell.template.processor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class JavaPackageProcessor implements TemplateProcessor {

    static final String TEMPLATE_PACKAGE = "example";

    @Override
    public Template process(Template template, Properties properties) {
        return template
                .withPath(outputPath(template, properties));
    }

    Path outputPath(Template template, Properties properties) {
        Path input = template.getPath();
        String pathStr = input.toString();
        String separator = File.separatorChar == '\\'
                ? "\\\\"
                : File.separator;
        String packagePath = packageToPath(properties.get("java.package"), separator).toString();
        if("\\\\".equals(separator)) {
            // we need even more slashes, or next replaceAll call will eat them alive
            packagePath = packagePath.replaceAll("\\\\", "\\\\\\\\");
        }
        pathStr = pathStr.replaceAll( separator + "?" + TEMPLATE_PACKAGE + separator, separator + packagePath + separator);
        return Paths.get(pathStr);
    }

    Path packageToPath(String packageName, String separator) {
        return Paths.get(packageName.replace(".", separator));
    }
}
