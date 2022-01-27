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


import io.bootique.tools.shell.template.BinaryFileLoader;
import io.bootique.tools.shell.template.BinaryResourceLoader;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.ExternalBinaryResourceLoader;
import io.bootique.tools.shell.template.TemplateExternalResourceLoader;
import io.bootique.tools.shell.template.TemplateLoader;
import io.bootique.tools.shell.template.TemplateResourceLoader;

public class LoaderFactory {
    public static TemplateLoader getLoaderWithType(LoaderType loaderType) {
        switch (loaderType) {
            case EMPTY:
                return new EmptyTemplateLoader();
            case TEMPLATE_RESOURCE:
                return new TemplateResourceLoader();
            case BINARY_FILE:
                return new BinaryFileLoader();
            case BINARY_RESOURCE:
                return new BinaryResourceLoader();
            case EXTERNAL_RESOURCE:
                return new TemplateExternalResourceLoader();
            case EXTERNAL_BINARY_RESOURCE:
                return new ExternalBinaryResourceLoader();
            default:
                throw new IllegalArgumentException("Unrecognizable loader type: " + loaderType);
        }
    }
}
