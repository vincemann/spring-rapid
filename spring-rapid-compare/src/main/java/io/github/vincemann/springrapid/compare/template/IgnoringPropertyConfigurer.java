package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;

public interface IgnoringPropertyConfigurer extends OperationConfigurer{

    IgnoringPropertyConfigurer ignore(Types.Supplier<?> getter);
    IgnoringPropertyConfigurer ignore(String propertyName);

}
