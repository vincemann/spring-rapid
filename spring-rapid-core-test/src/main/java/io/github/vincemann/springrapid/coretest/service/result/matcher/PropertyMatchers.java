package io.github.vincemann.springrapid.coretest.service.result.matcher;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.matcher.property.PropertyMatcher;

public class PropertyMatchers {

    /**
     * @param compareRoot supply own Entity
     * @return
     */
    public static PropertyMatcher propertyAssert(IdentifiableEntity compareRoot) {
        return new PropertyMatcher(compareRoot);
    }


}
