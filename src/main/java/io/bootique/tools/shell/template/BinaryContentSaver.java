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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.Set;

public class BinaryContentSaver implements TemplateSaver {

    private final Set<PosixFilePermission> permissions;

    public BinaryContentSaver() {
        this.permissions = Collections.emptySet();
    }

    public BinaryContentSaver(Set<PosixFilePermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void save(Template template, Properties properties) {
        if(!(template instanceof BinaryTemplate)) {
            throw new TemplateException("Template is not binary: " + template.getPath());
        }

        BinaryTemplate binaryTemplate = (BinaryTemplate)template;
        try {
            Files.createDirectories(template.getPath().getParent());
            try(OutputStream stream = Files
                    .newOutputStream(template.getPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                stream.write(binaryTemplate.getBinaryContent());
                stream.flush();
            }
        } catch (IOException ex) {
            throw new TemplateException("Can't process template " + template, ex);
        }

        if(!permissions.isEmpty()) {
            try {
                Files.setPosixFilePermissions(template.getPath(), permissions);
            } catch (UnsupportedOperationException | IOException ignore) {
            }
        }
    }
}
