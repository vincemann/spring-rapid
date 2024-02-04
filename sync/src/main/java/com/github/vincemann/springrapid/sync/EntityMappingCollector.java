package com.github.vincemann.springrapid.sync;

import java.util.Map;
import java.util.Set;

public interface EntityMappingCollector {

    public void collectEntityToDtoMappings();
    public Map<Class<?>, Set<Class<?>>> getEntityToDtoMappings();
}
