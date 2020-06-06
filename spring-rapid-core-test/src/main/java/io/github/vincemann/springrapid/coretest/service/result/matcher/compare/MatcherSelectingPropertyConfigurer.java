package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import com.github.hervian.reflection.Types;

public interface MatcherSelectingPropertyConfigurer extends MatcherOperationConfigurer{
    MatcherAdditionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    MatcherAdditionalSelectingPropertyConfigurer include(String propertyName);
}
