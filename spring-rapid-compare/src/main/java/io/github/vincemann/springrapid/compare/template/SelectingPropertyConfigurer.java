package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;

public interface SelectingPropertyConfigurer {
    OptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    OptionalSelectingPropertyConfigurer include(String propertyName);

}
