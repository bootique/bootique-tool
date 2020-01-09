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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateException;

public abstract class ParentFileProcessor implements TemplateProcessor {

    protected Shell shell;

    public ParentFileProcessor(Shell shell) {
        this.shell = shell;
    }

    protected abstract Charset detectCharset(byte[] content);

    protected abstract byte[] processParentFile(byte[] content,
                                                Charset charset,
                                                Properties properties) throws Exception;

    protected abstract void validateContent(BinaryTemplate template);

    protected Charset tryToDetectCharset(byte[] content) {
        if(content == null || content.length < 2) {
            return StandardCharsets.UTF_8;
        }
        // 1. Check BOM
        //        UTF-8 	    EF BB BF
        //        UTF-16 (BE)	FE FF
        //        UTF-16 (LE)	FF FE
        //        UTF-32 (BE)	00 00 FE FF
        //        UTF-32 (LE)	FF FE 00 00
        switch (content[0] & 0xFF) {
            case 0xEF:
                if((content[1] & 0xFF) == 0xBB && (content[2] & 0xFF) == 0xBF) {
                    return StandardCharsets.UTF_8;
                }
                break;
            case 0xFE:
                if((content[1] & 0xFF) == 0xFF) {
                    return StandardCharsets.UTF_16BE;
                }
            case 0xFF:
                if((content[1] & 0xFF) == 0xFE) {
                    if(content[2] == 0x00 && content[3] == 0x00) {
                        return Charset.forName("UTF-32LE");
                    } else {
                        return StandardCharsets.UTF_16LE;
                    }
                }
            case 0x00:
                if(content[1] == 0x00 && (content[2] & 0xFF) == 0xFE && (content[3] & 0xFF) == 0xFF) {
                    return Charset.forName("UTF-32BE");
                }
        }

        return null;
    }

    @Override
    public Template process(Template template, Properties properties) {
        BinaryTemplate binaryTemplate = (BinaryTemplate)template;
        validateContent(binaryTemplate);
        // backup parent build file
        Path backup = template.getPath()
                .getParent()
                .resolve(template
                        .getPath()
                        .getFileName().toString() + ".bq-backup");

        try {
            Files.copy(template.getPath(), backup, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new TemplateException("Unable to create parent build file backup", ex);
        }

        try {
            byte[] content = binaryTemplate.getBinaryContent();
            Charset charset = detectCharset(content);
            byte[] modifiedContent = processParentFile(content, charset, properties);
            Files.write(template.getPath(), modifiedContent, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (Exception ex) {
            // rollback parent build file
            try {
                Files.move(backup, template.getPath(), StandardCopyOption.ATOMIC_MOVE ,StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex2) {
                ex.addSuppressed(ex2);
            }
            throw new TemplateException("Unable to update parent build file", ex);
        } finally {
            try {
                Files.delete(backup);
            } catch (IOException ex3) {
                // todo: should log this...
            }
        }
        return template;
    }
}
