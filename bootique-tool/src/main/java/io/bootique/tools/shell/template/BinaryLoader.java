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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @since 4.2
 */
public abstract class BinaryLoader implements TemplateLoader {
    byte[] loadContent(InputStream stream) throws IOException {
        byte[] content = null;
        try(BufferedInputStream bis = new BufferedInputStream(stream)) {
            int available;
            while((available = bis.available()) != 0) {
                byte[] buffer = new byte[available];
                int read = bis.read(buffer);
                if(read == 0) {
                    break;
                }
                if(content == null) {
                    content = buffer;
                } else {
                    byte[] newContent = new byte[content.length + read];
                    System.arraycopy(content, 0, newContent, 0, content.length);
                    System.arraycopy(buffer, 0, newContent, content.length, buffer.length);
                    content = newContent;
                }
            }
        }
        return content;
    }
}
