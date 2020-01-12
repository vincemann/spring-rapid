package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;

public interface TestConfigurationFactory<C> {
    public abstract C createDefaultConfig();
    public abstract C createMergedConfig(C modification) throws InvalidConfigurationModificationException;
}
