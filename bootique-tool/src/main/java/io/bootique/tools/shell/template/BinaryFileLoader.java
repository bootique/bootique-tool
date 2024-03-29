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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class BinaryFileLoader extends BinaryLoader {

    @Override
    public Template load(String source, Properties properties) {
        try(InputStream stream = new FileInputStream(source)) {
            byte[] content;
            try {
                content = loadContent(stream);
            } catch (IOException ex) {
                throw new TemplateException("Unable to read resource " + source, ex);
            }
            return new BinaryTemplate(Paths.get(source), content);
        } catch (FileNotFoundException ex) {
            throw new TemplateException("Unable to read resource " + source + ". File not found.", ex);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }
    }
}
