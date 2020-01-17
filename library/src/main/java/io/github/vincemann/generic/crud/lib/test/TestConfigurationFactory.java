package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;

public interface TestConfigurationFactory<SuccessfulC,FailedC, SuccessfulMod, FailedMod> {
    public abstract SuccessfulC createSuccessfulDefaultConfig();
    public abstract SuccessfulC createSuccessfulMergedConfig(SuccessfulMod modification) throws InvalidConfigurationModificationException;
    public abstract FailedC createFailedDefaultConfig();
    public abstract FailedC createFailedMergedConfig(FailedMod modification) throws InvalidConfigurationModificationException;
}
