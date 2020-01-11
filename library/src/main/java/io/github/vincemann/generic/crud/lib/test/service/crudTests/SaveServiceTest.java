package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.abs.AbstractServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.save.SuccessfulSaveTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save.SuccessfulSaveTestConfiguration;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class SaveServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTestApi<E,Id>
{

    private AbstractTestConfigurationFactory<E,Id,SuccessfulSaveTestConfiguration<E,Id>> successfulSaveTestConfigurationFactory;
    //private FailedSaveTestConfigurationFactory<E,Id> failedSaveTestConfigurationFactory;

    public SaveServiceTest(RootServiceTestContext<E, Id> testContext) {
        super(testContext);
        this.successfulSaveTestConfigurationFactory=  new SuccessfulSaveTestConfigurationFactory<>(testContext);
    }

    public E saveEntity_ShouldSucceed(E entityToSave) throws BadEntityException {
        try {
            return saveEntity_ShouldSucceed(entityToSave, successfulSaveTestConfigurationFactory.createDefaultConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }

    public E saveEntity_ShouldSucceed(E entityToSave, SuccessfulSaveTestConfiguration<E,Id> configModification) throws BadEntityException, InvalidConfigurationModificationException {
        SuccessfulSaveTestConfiguration<E, Id> config = successfulSaveTestConfigurationFactory.createMergedConfig(configModification);
        //given
        Assertions.assertNull(entityToSave.getId());

        //when
        E savedTestEntity = getRootContext().serviceSave(entityToSave);

        //then
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());

        //if service save method does not copy entityToSave, then entitytoSaveRef = savedEntityFromServiceRef, otherwise not
        Optional<E> repoEntity = getRootContext().repoFindById(savedTestEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());

        Id oldId_EntityToSave = entityToSave.getId();
        entityToSave.setId(savedTestEntity.getId());

        Assertions.assertTrue(config.getRepoEntityEqualChecker().isEqual(entityToSave,repoEntity.get()));
        Assertions.assertTrue(config.getReturnedEntityEqualChecker().isEqual(entityToSave,savedTestEntity));

        //restore old id
        entityToSave.setId(oldId_EntityToSave);

        return savedTestEntity;
    }

    public <T extends Throwable> T saveEntity_ShouldFail(E entityToSave, Class<? extends T> expectedException) {
        return Assertions.assertThrows(expectedException, () -> getRootContext().serviceSave(entityToSave));
    }
}
