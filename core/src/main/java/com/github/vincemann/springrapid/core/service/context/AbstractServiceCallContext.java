package com.github.vincemann.springrapid.core.service.context;

import java.util.HashMap;
import java.util.Map;

public class AbstractServiceCallContext {

    protected Map<String,Object> values = new HashMap<>();

    public void setValue(String key, Object value) {
        values.put(key,value);
    }

    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

    public void clearValue(String key) {
        values.remove(key);
    }
}
