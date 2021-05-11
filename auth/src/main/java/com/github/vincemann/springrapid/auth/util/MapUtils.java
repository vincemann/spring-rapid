package com.github.vincemann.springrapid.auth.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    /**
     * Constructs a map of the key-value pairs,
     * passed as parameters
     *
     * @param keyValPair
     */
    @SuppressWarnings("unchecked")
    public static <K,V> Map<K,V> mapOf(Object... keyValPair) {

        if(keyValPair.length % 2 != 0)
            throw new IllegalArgumentException("Keys and values must be in pairs");

        Map<K,V> map = new HashMap<K,V>(keyValPair.length / 2);

        for(int i = 0; i < keyValPair.length; i += 2){
            map.put((K) keyValPair[i], (V) keyValPair[i+1]);
        }

        return map;
    }
}
