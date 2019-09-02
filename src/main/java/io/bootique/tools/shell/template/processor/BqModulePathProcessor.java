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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class BqModulePathProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        // Replace MyModule in name to real module name
        String moduleName = properties.get("module.name");
        Path tplPath = template.getPath();

        Iterator<Path> pathIterator = tplPath.iterator();
        Path finalPath = Paths.get("/");
        while(pathIterator.hasNext()) {
            String next = pathIterator.next().toString();
            if(next.contains("MyModule")) {
                next = next.replace("MyModule", moduleName);
            }
            finalPath = finalPath.resolve(next);
        }

        return template.withPath(finalPath);
    }
}
