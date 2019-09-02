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

package org.jline.utils;

/**
 * Stub implementation of JLine Signals helpers.
 */
public final class Signals {

    private Signals() {
    }

    public static Object register(String name, Runnable handler) {
        return null;
    }

    public static Object register(String name, final Runnable handler, ClassLoader loader) {
        return null;
    }

    public static Object registerDefault(String name) {
        return null;
    }

    public static void unregister(String name, Object previous) {
    }

}
