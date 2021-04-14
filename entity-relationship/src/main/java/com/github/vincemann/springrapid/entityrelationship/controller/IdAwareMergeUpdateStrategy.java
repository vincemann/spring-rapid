package com.github.vincemann.springrapid.entityrelationship.controller;

import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategyImpl;
import com.github.vincemann.springrapid.core.util.EntityCollectionUtils;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * Translates i.E. ownerId to owner when merging.
 */
public class IdAwareMergeUpdateStrategy extends MergeUpdateStrategyImpl {

    @Override
    protected String transform(String dtoPropertyName) {
        return EntityCollectionUtils.transformDtoCollectionFieldName(dtoPropertyName);
    }

}
