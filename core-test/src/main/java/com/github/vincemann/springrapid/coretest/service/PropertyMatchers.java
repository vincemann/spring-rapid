package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.coretest.service.result.matcher.property.PropertyMatcher;

public class PropertyMatchers {

    /**
     * @param compareRoot supply own Entity
     * @return
     */
    public static PropertyMatcher propertyAssert(Object compareRoot) {
        return new PropertyMatcher(compareRoot);
    }


}
