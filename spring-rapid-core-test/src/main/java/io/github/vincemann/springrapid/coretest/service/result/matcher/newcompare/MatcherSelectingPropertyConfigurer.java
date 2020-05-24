package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.compare.template.OptionalSelectingPropertyConfigurer;

public interface MatcherSelectingPropertyConfigurer extends MatcherOperationConfigurer{
    MatcherOptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    MatcherOptionalSelectingPropertyConfigurer include(String propertyName);
}
