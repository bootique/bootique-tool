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

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class BqModuleNameProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        String content = template.getContent();
        content = replaceModuleName(content, properties);
        return template.withContent(content);
    }

    private String replaceModuleName(String content, Properties properties) {
        String moduleName = properties.get("module.name");
//        content = content.replaceFirst("(?m)^public class MyModule(.*)$", "public class " + moduleName + "$1");
//        content = content.replaceFirst("(?m)^(\\s+)return new MyModule\\(\\)(.*)$", "$1return new " + moduleName + "()$2");
        content = content.replaceAll("MyModule", moduleName);
        return content;
    }
}
