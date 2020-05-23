package io.github.vincemann.springrapid.compare.template;

public interface PropertyConfigurer extends SelectingPropertyConfigurer{

    IgnoringPropertyConfigurer allOf(Object o);
    IgnoringPropertyConfigurer all();

}
