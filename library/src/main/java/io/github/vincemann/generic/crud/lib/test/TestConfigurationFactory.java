package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;

public interface TestConfigurationFactory<SuccessfulC,FailedC> {
    public abstract SuccessfulC createSuccessfulDefaultConfig();
    public abstract SuccessfulC createSuccessfulMergedConfig(SuccessfulC modification) throws InvalidConfigurationModificationException;
    public abstract FailedC createFailedDefaultConfig();
    public abstract FailedC createFailedMergedConfig(FailedC modification) throws InvalidConfigurationModificationException;
}
