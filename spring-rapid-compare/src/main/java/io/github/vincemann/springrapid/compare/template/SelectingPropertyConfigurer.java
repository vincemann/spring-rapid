package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;

public interface SelectingPropertyConfigurer {
    AdditionalSelectingPropertyConfigurer include(Types.Supplier<?> getter);
    AdditionalSelectingPropertyConfigurer include(String propertyName);

}
