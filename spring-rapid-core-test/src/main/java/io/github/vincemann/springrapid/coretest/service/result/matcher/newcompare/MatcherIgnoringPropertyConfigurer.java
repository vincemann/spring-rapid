package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.compare.template.IgnoringPropertyConfigurer;

public interface MatcherIgnoringPropertyConfigurer extends MatcherOperationConfigurer {
    MatcherIgnoringPropertyConfigurer ignore(Types.Supplier<?> getter);
    MatcherIgnoringPropertyConfigurer ignore(String propertyName);
}
