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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Properties {

    private final Map<String, Object> props;

    public static Builder builder() {
        return new Builder();
    }

    private Properties(Map<String, Object> props) {
        this.props = props;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T)props.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue) {
        return (T)props.getOrDefault(name, defaultValue);
    }

    public Map<String, Object> asMap() {
        return props;
    }

    public static class Builder {

        private final Map<String, Object> props = new HashMap<>();

        public Builder with(String name, Object value) {
            props.put(name, value);
            return this;
        }

        public Properties build() {
            return new Properties(Collections.unmodifiableMap(props));
        }
    }

}
