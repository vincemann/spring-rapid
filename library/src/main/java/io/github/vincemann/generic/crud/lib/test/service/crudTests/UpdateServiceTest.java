package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.abs.AbstractServiceTest;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.ServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.UpdateServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.FailedUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.SuccessfulUpdateServiceTestConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
public class UpdateServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTest<E, Id> {
    private AbstractServiceTestConfigurationFactory<E, Id, SuccessfulUpdateServiceTestConfiguration<E, Id>,FailedUpdateServiceTestConfiguration<E, Id>> updateTestConfigurationFactory;

    public UpdateServiceTest(ServiceTest<E, Id> testContext) {
        super(testContext);
        this.updateTestConfigurationFactory = new UpdateServiceTestConfigurationFactory<>(testContext);
    }


    public E updateEntity_ShouldSucceed(E entityToUpdate, E updateRequest) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate, updateRequest);
        try {
            return updateEntity_ShouldSucceed(updateRequest, updateTestConfigurationFactory.createSuccessfulDefaultConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }

    public E updateEntity_ShouldSucceed(E entityToUpdate, E updateRequest, ServiceTestConfiguration<E, Id> configModification) throws BadEntityException, EntityNotFoundException, NoIdException, InvalidConfigurationModificationException {
        saveEntityForUpdate(entityToUpdate, updateRequest);
        return updateEntity_ShouldSucceed(updateRequest, configModification);
    }

    public E updateEntity_ShouldSucceed(E updateRequest) throws BadEntityException, EntityNotFoundException, NoIdException {
        try {
            return updateEntity_ShouldSucceed(updateRequest, updateTestConfigurationFactory.createSuccessfulDefaultConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }


    public E updateEntity_ShouldSucceed(E updateRequest, ServiceTestConfiguration<E, Id> configModification) throws NoIdException, BadEntityException, EntityNotFoundException, InvalidConfigurationModificationException {
        SuccessfulUpdateServiceTestConfiguration<E, Id> config = updateTestConfigurationFactory.createSuccessfulMergedConfig(configModification);
        //given
        Assertions.assertNotNull(updateRequest);
        Assertions.assertNotNull(updateRequest.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(updateRequest.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(config.getRepoEntityEqualChecker().isEqual(updateRequest, entityToUpdate.get()));

        //when
        E updatedEntity = getRootContext().serviceUpdate(updateRequest, config.getFullUpdate());

        //then
        Assertions.assertEquals(updatedEntity.getId(), updateRequest.getId());
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(updatedEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());


        Assertions.assertTrue(config.getRepoEntityEqualChecker().isEqual(updateRequest, updatedEntity));
        Optional<E> updatedEntityFromRepo = getRootContext().repoFindById(updateRequest.getId());
        Assertions.assertTrue(updatedEntityFromRepo.isPresent());
        Assertions.assertTrue(config.getReturnedEntityEqualChecker().isEqual(updateRequest, updatedEntity));
        config.getPostUpdateCallback().callback(updateRequest, updatedRepoEntity.get());

        return updatedEntity;
    }


    public <T extends Throwable> T updateEntity_ShouldFail(E newEntity) {
        try {
            return (T) updateEntity_ShouldFail(newEntity, updateTestConfigurationFactory.createFailedDefaultConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Throwable> T updateEntity_ShouldFail(E entityToUpdate, E newEntity, ServiceTestConfiguration<E, Id> configModification) throws InvalidConfigurationModificationException {
        saveEntityForUpdate(entityToUpdate, newEntity);
        return updateEntity_ShouldFail(newEntity, configModification);
    }

    public <T extends Throwable> T updateEntity_ShouldFail(E entityToUpdate, E newEntity) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate, newEntity);
        try {
            return (T) updateEntity_ShouldFail(newEntity, updateTestConfigurationFactory.createFailedDefaultConfig());
        } catch (InvalidConfigurationModificationException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Throwable> T updateEntity_ShouldFail(E newEntity, ServiceTestConfiguration<E, Id> configModification) throws InvalidConfigurationModificationException {
        FailedUpdateServiceTestConfiguration<E, Id> config = updateTestConfigurationFactory.createFailedMergedConfig(configModification);
        //given
        //entity to update is present
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(config.getRepoEntityEqualChecker().isEqual(newEntity, entityToUpdate.get()));

        //when
        T exception = (T) Assertions.assertThrows(config.getExpectedException(), () -> getRootContext().serviceUpdate(newEntity, config.getFullUpdate()));

        //then
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        //still the same
        Assertions.assertTrue(config.getRepoEntityEqualChecker().isEqual(entityToUpdate.get(), updatedRepoEntity.get()));
        config.getPostUpdateCallback().callback(newEntity, updatedRepoEntity.get());
        return exception;
    }

    private void saveEntityForUpdate(E entityToUpdate, E newEntity) {
        Assertions.assertNull(entityToUpdate.getId());
        Assertions.assertNull(newEntity.getId());
        E savedEntityToUpdate = getRootContext().repoSave(entityToUpdate);
        newEntity.setId(savedEntityToUpdate.getId());
    }

}
