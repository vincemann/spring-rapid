package com.github.vincemann.springrapid.autobidir.controller;

import com.github.vincemann.springrapid.core.controller.MergeUpdateStrategyImpl;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;


/**
 * Translates i.E. 'ownerId' to 'owner', 'petIds' to 'pets' when merging.
 */
public class IdAwareMergeUpdateStrategy extends MergeUpdateStrategyImpl {

    @Override
    protected String transform(String dtoPropertyName) {
        return IdPropertyNameUtils.transformIdFieldName(dtoPropertyName);
    }

}
