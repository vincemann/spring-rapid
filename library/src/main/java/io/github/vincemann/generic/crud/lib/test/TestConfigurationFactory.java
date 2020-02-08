package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;

public interface TestConfigurationFactory<SuccessfulC,FailedC, SuccessfulMod, FailedMod> {
    public abstract SuccessfulC createDefaultSuccessfulConfig();
    public abstract SuccessfulC createMergedSuccessfulConfig(SuccessfulMod... modifications) throws InvalidConfigurationModificationException;
    public abstract FailedC createDefaultFailedConfig();
    public abstract FailedC createMergedFailedConfig(FailedMod... modifications) throws InvalidConfigurationModificationException;
}
