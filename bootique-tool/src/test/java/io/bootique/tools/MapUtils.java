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

package io.bootique.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapUtils {

    public static <K,V> Map<K, V> mapOf(Object... args) {
        if(args.length == 0) {
            return Collections.emptyMap();
        }

        if(args.length == 2) {
            @SuppressWarnings("unchecked")
            K k = Objects.requireNonNull((K)args[0]);
            @SuppressWarnings("unchecked")
            V v = Objects.requireNonNull((V)args[1]);
            return Collections.singletonMap(k, v);
        }

        Map<K,V> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            @SuppressWarnings("unchecked")
            K k = Objects.requireNonNull((K)args[i]);
            @SuppressWarnings("unchecked")
            V v = Objects.requireNonNull((V)args[i+1]);
            map.put(k, v);
        }
        return map;
    }

}
