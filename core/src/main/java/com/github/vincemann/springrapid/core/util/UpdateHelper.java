package com.github.vincemann.springrapid.core.util;

import org.apache.logging.log4j.util.TriConsumer;

import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

public class UpdateHelper {
    private Validator validator;

    public UpdateHelper(Validator validator) {
        this.validator = validator;
    }

    public void copyProperties(Object src, Object dst){
        copyProperties(src,dst,new HashSet<>());
    }

    public void copyProperties(Object src, Object dst, Set<String> properties){
        NullAwareBeanUtils.copyProperties(dst, src, properties, new TriConsumer<Object, String, Object>() {
            @Override
            public void accept(Object origin, String property, Object value) {
                // hooks all property update calls that are actually updated
                validator.validateProperty(origin,property);
            }
        });
    }
}
