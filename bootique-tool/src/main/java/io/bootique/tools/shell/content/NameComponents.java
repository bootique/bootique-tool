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

package io.bootique.tools.shell.content;

public class NameComponents {

    private final String javaPackage;

    private final String name;

    private final String version;

    public NameComponents(String javaPackage, String name, String version) {
        this.javaPackage = javaPackage;
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public String getVersion() {
        return version;
    }

    public NameComponents withName(String name) {
        return new NameComponents(javaPackage, name, version);
    }

    public NameComponents withJavaPackage(String javaPackage) {
        return new NameComponents(javaPackage, name, version);
    }

    public NameComponents withVersion(String version) {
        return new NameComponents(javaPackage, name, version);
    }

    @Override
    public String toString() {
        return javaPackage + ':' + name + ':' + version;
    }
}
