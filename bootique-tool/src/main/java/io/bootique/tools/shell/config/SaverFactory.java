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

package io.bootique.tools.shell.config;

import io.bootique.tools.shell.template.BinaryContentSaver;
import io.bootique.tools.shell.template.SafeBinaryContentSaver;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplateFileSaver;
import io.bootique.tools.shell.template.TemplateSaver;

public class SaverFactory {
    private FilePermissions permissions;

    public SaverFactory() {
    }

    public void setPermissions(FilePermissions permissions) {
        this.permissions = permissions;
    }

    public TemplateSaver getSaverWithType(SaverType saverType) {
        switch (saverType) {
            case FILE:
                return new TemplateFileSaver();
            case BINARY: {
                if (permissions != null) {
                    return new BinaryContentSaver(permissions.toPosixFilePermissions());
                }
                return new BinaryContentSaver();
            }
            case SAFE_BINARY:
                return new SafeBinaryContentSaver();
            case DIR_ONLY:
                return new TemplateDirOnlySaver();
            default:
                throw new IllegalArgumentException("Unrecognizable saver type: " + saverType);
        }
    }
}
