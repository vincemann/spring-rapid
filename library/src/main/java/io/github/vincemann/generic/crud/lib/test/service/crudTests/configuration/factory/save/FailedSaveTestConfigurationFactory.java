package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.save;

/*
public class FailedSaveTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfigurationFactory<E, Id, FailedSaveAbstractTestConfiguration<E,Id>> {

    public FailedSaveTestConfigurationFactory(RootServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedSaveAbstractTestConfiguration<E, Id> createDefaultConfig() {
        return new FailedSaveAbstractTestConfiguration<>(getContext().getDefaultEqualChecker());
    }

    @Override
    public FailedSaveAbstractTestConfiguration<E, Id> createMergedConfig(FailedSaveAbstractTestConfiguration<E, Id> modification) {
        FailedSaveAbstractTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
*/