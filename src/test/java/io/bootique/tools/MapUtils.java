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
