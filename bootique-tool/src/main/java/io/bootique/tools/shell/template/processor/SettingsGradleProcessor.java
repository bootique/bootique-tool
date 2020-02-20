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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;

public class SettingsGradleProcessor extends ParentFileProcessor {

    public SettingsGradleProcessor(Shell shell) {
        super(shell);
    }

    @Override
    protected Charset detectCharset(byte[] content) {
        Charset detectedCharset = tryToDetectCharset(content);
        return detectedCharset != null ? detectedCharset : Charset.defaultCharset();
    }

    @Override
    protected byte[] processParentFile(byte[] content, Charset charset, Properties properties) {
        String moduleDefinition = "\ninclude '" + properties.get("project.name") + "'";
        ByteBuffer byteBuffer = charset.encode(moduleDefinition);
        byte[] moduleDefinitionBinary = new byte[byteBuffer.limit()];
        byteBuffer.get(moduleDefinitionBinary);

        int offset = content.length;

        byte[] modifiedContent = new byte[content.length + moduleDefinitionBinary.length];
        System.arraycopy(content, 0, modifiedContent, 0, offset);
        System.arraycopy(moduleDefinitionBinary, 0, modifiedContent, offset, moduleDefinitionBinary.length);

        return modifiedContent;
    }

    @Override
    protected void validateContent(BinaryTemplate template, Charset charset) {
        // do nothing
    }
}
