package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.abs.AbstractServiceTest;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory.SaveServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.SuccessfulSaveServiceTestConfiguration;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class SaveServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTest<E,Id>
{

    private AbstractServiceTestConfigurationFactory<E,Id, SuccessfulSaveServiceTestConfiguration<E,Id>, ServiceTestConfiguration<E,Id>> saveTestConfigurationFactory;

    public SaveServiceTest(ServiceTest<E, Id> testContext) {
        super(testContext);
        this.saveTestConfigurationFactory =  new SaveServiceTestConfigurationFactory<>(testContext);
    }

    public E saveEntity_ShouldSucceed(E entityToSave) throws BadEntityException {
        try {
            return saveEntity_ShouldSucceed(entityToSave, saveTestConfigurationFactory.createDefaultSuccessfulConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }

    public E saveEntity_ShouldSucceed(E entityToSave, ServiceTestConfiguration<E,Id>... configModification) throws BadEntityException, InvalidConfigurationModificationException {
        SuccessfulSaveServiceTestConfiguration<E, Id> config = saveTestConfigurationFactory.createMergedSuccessfulConfig(configModification);
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
