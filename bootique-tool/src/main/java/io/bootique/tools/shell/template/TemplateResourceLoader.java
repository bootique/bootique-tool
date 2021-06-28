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

package io.bootique.tools.shell.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateResourceLoader implements TemplateLoader, ResourceLoader {

    @Override
    public Template load(String source, Properties properties) {
        String basePath = properties.get("input.path");

        InputStream stream = getResourceAsStream(basePath + source);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + basePath + source+" from tool resources;" +
                    " maybe you need\n to use external resource loader for this instead\n" +
                    " or yor forgot to set defaultLoader property?");
        }

        StringBuilder content = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }

        Path output = properties.get("output.path");
        return new Template(output.resolve(source), content.toString());
    }
}
