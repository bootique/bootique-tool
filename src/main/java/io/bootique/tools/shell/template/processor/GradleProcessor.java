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

import java.util.HashMap;
import java.util.Map;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;


public class GradleProcessor implements TemplateProcessor {

    private Map<String, String> gradleKeyReplacement = new HashMap<>();

    public GradleProcessor() {
        // keys to replace in .gradle files
        gradleKeyReplacement.put("group",                   "java.package");
        gradleKeyReplacement.put("version",                 "project.version");
        gradleKeyReplacement.put("rootProject.name",        "project.name");
        gradleKeyReplacement.put("mainClassName",           "project.mainClass");
        gradleKeyReplacement.put("implementation platform", "bq.version");
    }

    @Override
    public Template process(Template template, Properties properties) {
        String[] lines = template.getContent().split("\n");
        String[] alternateLines = new String[lines.length];

        lines:
        for(int i=0; i<lines.length; i++) {
            String line = lines[i];
            for(Map.Entry<String, String> replacement: gradleKeyReplacement.entrySet()) {
                if(line.startsWith(replacement.getKey())) {
                    String value = properties.get(replacement.getValue());
                    if("implementation platform".equals(replacement.getKey())) {
                        value = "io.bootique.bom:bootique-bom:" + value;
                    }

                    int valueStart = line.indexOf("'");
                    int valueEnd = line.indexOf("'", valueStart + 1);
                    alternateLines[i] = line.substring(0, valueStart + 1)
                            + value
                            + line.substring(valueEnd);
                    continue lines;
                }
            }
            alternateLines[i] = line;
        }

        return template.withContent(String.join("\n", alternateLines));
    }
}
