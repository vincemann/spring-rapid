package io.github.vincemann.springrapid.coretest.compare.comparator;

import com.github.hervian.reflection.Types;

public interface PropertyConfigurer extends SelectingPropertyConfigurer{

    IgnoringPropertyConfigurer allOf(Object o);
    IgnoringPropertyConfigurer all();

}
