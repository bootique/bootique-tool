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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class BinaryResourceLoader extends BinaryLoader implements ResourceLoader {

    @Override
    public BinaryTemplate load(String source, Properties properties) {
        String basePath = properties.get("input.path");

        InputStream stream = getResourceAsStream(basePath + source);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + basePath + source);
        }

        byte[] content;
        try {
            content = loadContent(stream);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }

        Path output = properties.get("output.path");
        return new BinaryTemplate(output.resolve(source), content);
    }
}
