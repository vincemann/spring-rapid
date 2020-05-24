package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import com.github.hervian.reflection.Types;

public interface MatcherIgnoringPropertyConfigurer extends MatcherOperationConfigurer {
    MatcherIgnoringPropertyConfigurer ignore(Types.Supplier<?> getter);
    MatcherIgnoringPropertyConfigurer ignore(String propertyName);
}
