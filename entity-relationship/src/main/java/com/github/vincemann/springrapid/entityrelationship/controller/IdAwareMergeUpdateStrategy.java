package com.github.vincemann.springrapid.entityrelationship.controller;

import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategyImpl;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * Translates i.E. ownerId to owner when merging.
 */
public class IdAwareMergeUpdateStrategy extends MergeUpdateStrategyImpl {
    public static final String ID_SUFFIX = "Id";
    public static final String IDS_SUFFIX = "Ids";

    @Override
    protected String transform(String dtoPropertyName) {
        if (dtoPropertyName.endsWith(ID_SUFFIX)) {
            return dtoPropertyName.substring(0, dtoPropertyName.length() - ID_SUFFIX.length());
        }
        else if (dtoPropertyName.endsWith(IDS_SUFFIX)) {
            return dtoPropertyName.substring(0, dtoPropertyName.length() - IDS_SUFFIX.length()) +"s";
        }else {
            return dtoPropertyName;
        }
    }

}
