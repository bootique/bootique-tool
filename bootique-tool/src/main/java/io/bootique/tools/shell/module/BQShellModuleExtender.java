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

package io.bootique.tools.shell.module;

import io.bootique.ModuleExtender;
import io.bootique.di.Binder;
import io.bootique.di.MapBuilder;
import io.bootique.tools.shell.content.ContentHandler;

public class BQShellModuleExtender extends ModuleExtender<BQShellModuleExtender> {

    private MapBuilder<String, ContentHandler> contentHandlers;

    public BQShellModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public BQShellModuleExtender initAllExtensions() {
        contributeArtifactHandlers();
        return this;
    }

    public BQShellModuleExtender addHandler(String name, Class<? extends ContentHandler> handler) {
        contributeArtifactHandlers().put(name, handler);
        return this;
    }

    public BQShellModuleExtender addHandler(String name, ContentHandler handler) {
        contributeArtifactHandlers().putInstance(name, handler);
        return this;
    }

    protected MapBuilder<String, ContentHandler> contributeArtifactHandlers() {
        // no synchronization. we don't care if it is created twice. It will still work with Guice.
        return contentHandlers != null
                ? contentHandlers
                : (contentHandlers = newMap(String.class, ContentHandler.class));
    }
}
