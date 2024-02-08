package com.github.vincemann.springrapid.core.service.ctx;

import com.github.vincemann.springrapid.core.CoreProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class CoreContextService implements ContextService {

    private CoreProperties properties;

    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("shared", properties.getShared());
        return context;
    }

    @Autowired
    public void setProperties(CoreProperties properties) {
        this.properties = properties;
    }
}
