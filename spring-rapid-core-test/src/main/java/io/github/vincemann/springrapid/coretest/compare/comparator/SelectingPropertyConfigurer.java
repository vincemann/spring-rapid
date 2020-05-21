package io.github.vincemann.springrapid.coretest.compare.comparator;

import com.github.hervian.reflection.Types;

public interface SelectingPropertyConfigurer {
    OptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    OptionalSelectingPropertyConfigurer include(String propertyName);

}
