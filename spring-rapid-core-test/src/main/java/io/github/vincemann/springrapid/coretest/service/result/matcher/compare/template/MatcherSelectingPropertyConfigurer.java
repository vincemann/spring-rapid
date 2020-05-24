package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import com.github.hervian.reflection.Types;

public interface MatcherSelectingPropertyConfigurer extends MatcherOperationConfigurer{
    MatcherOptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    MatcherOptionalSelectingPropertyConfigurer include(String propertyName);
}
