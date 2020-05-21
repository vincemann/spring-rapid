package io.github.vincemann.springrapid.coretest.compare.comparator;

import com.github.hervian.reflection.Types;

public interface IgnoringPropertyConfigurer {

    IgnoringPropertyConfigurer ignore(Types.Supplier<?> getter);
    IgnoringPropertyConfigurer ignore(String propertyName);

}
