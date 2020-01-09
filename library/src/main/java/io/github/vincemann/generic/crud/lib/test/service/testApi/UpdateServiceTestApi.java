package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.AbstractServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.update.AbstractUpdateTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.update.FailedUpdateTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.update.SuccessfulUpdateTestConfiguration;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

@Getter
public class UpdateServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
        extends AbstractServiceTestApi<E,Id,R>
{
    public static final String PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER = "partialUpdateEqualCheckerBean";
    private EqualChecker<E> defaultPartialUpdateEqualChecker;

    public UpdateServiceTestApi(RootServiceTestContext<E, Id, R> serviceTestContext) {
        super(serviceTestContext);
    }


    @Autowired
    @Qualifier(PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER)
    protected void injectPartialUpdateEqualChecker(EqualChecker<E> partialUpdateEqualChecker){
        this.defaultPartialUpdateEqualChecker =partialUpdateEqualChecker;
    }


    public E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity, SuccessfulUpdateTestConfiguration<E,Id> successfulTestContext) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity, successfulTestContext);
    }

    //todo maybe put this stuff in an own class
    protected void adjustConfiguration(SuccessfulUpdateTestConfiguration<E,Id> configuration){
        if(!configuration.isFullUpdate()){
            if(configuration.getReturnedEntityEqualChecker()==null){
                configuration.setReturnedEntityEqualChecker(getDefaultPartialUpdateEqualChecker());
            }
        }else {
            if (configuration.getReturnedEntityEqualChecker() == null) {
                configuration.setReturnedEntityEqualChecker(getRootContext().getDefaultEqualChecker());
            }
        }
        adjustAbstractConfiguration(configuration);
    }

    protected void adjustAbstractConfiguration(AbstractUpdateTestConfiguration<E,Id> configuration){
        if(!configuration.isFullUpdate()){
            //partial update
            if(configuration.getRepoEntityEqualChecker()==null){
                configuration.setRepoEntityEqualChecker(getDefaultPartialUpdateEqualChecker());
            }

        }else {
            //full update
            if (configuration.getRepoEntityEqualChecker() == null) {
                configuration.setRepoEntityEqualChecker(getRootContext().getDefaultEqualChecker());
            }
        }
        if(configuration.getPostUpdateCallback()==null){
            //prevent nullpointer, makes test code cleaner
            configuration.setPostUpdateCallback((r,a)->{});
        }
    }

    protected void adjustConfiguration(FailedUpdateTestConfiguration<E,Id> configuration){
        if(configuration.getExpectedException()==null){
            configuration.setExpectedException(Exception.class);
        }
        adjustAbstractConfiguration(configuration);
    }



    public E updateEntity_ShouldSucceed(E updateRequest, SuccessfulUpdateTestConfiguration<E,Id> successfulTestContext) throws NoIdException, BadEntityException, EntityNotFoundException {
        adjustConfiguration(successfulTestContext);
        //given
        Assertions.assertNotNull(updateRequest);
        Assertions.assertNotNull(updateRequest.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(updateRequest.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(successfulTestContext.getRepoEntityEqualChecker().isEqual(updateRequest,entityToUpdate.get()));

        //when
        E updatedEntity = getRootContext().serviceUpdate(updateRequest, successfulTestContext.isFullUpdate());

        //then
        Assertions.assertEquals(updatedEntity.getId(),updateRequest.getId());
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(updatedEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());


        Assertions.assertTrue(successfulTestContext.getRepoEntityEqualChecker().isEqual(updateRequest,updatedEntity));
        Optional<E> updatedEntityFromRepo = getRootContext().repoFindById(updateRequest.getId());
        Assertions.assertTrue(updatedEntityFromRepo.isPresent());
        Assertions.assertTrue(successfulTestContext.getReturnedEntityEqualChecker().isEqual(updateRequest,updatedEntity));
        successfulTestContext.getPostUpdateCallback().callback(updateRequest,updatedRepoEntity.get());

        return updatedEntity;
    }



    public <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity) throws NoIdException {
        return (T) updateExistingEntity_ShouldFail(newEntity,getDefaultFailedContext());
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, FailedUpdateTestConfiguration<E,Id> failedTestContext) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateExistingEntity_ShouldFail(newEntity,failedTestContext);
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return (T) updateExistingEntity_ShouldFail(newEntity,getDefaultFailedContext());
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity,  FailedUpdateTestConfiguration<E,Id> failedTestContext) throws NoIdException{
        adjustConfiguration(failedTestContext);
        //given
        //entity to update is present
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(failedTestContext.getRepoEntityEqualChecker().isEqual(newEntity,entityToUpdate.get()));

        //when
        T exception = (T) Assertions.assertThrows(failedTestContext.getExpectedException(), () -> getRootContext().serviceUpdate(newEntity,failedTestContext.isFullUpdate()));

        //then
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        //still the same
        Assertions.assertTrue(failedTestContext.getRepoEntityEqualChecker().isEqual(entityToUpdate.get(),updatedRepoEntity.get()));
        failedTestContext.getPostUpdateCallback().callback(newEntity,updatedRepoEntity.get());
        return exception;
    }

    private void saveEntityForUpdate(E entityToUpdate, E newEntity) {
        Assertions.assertNull(entityToUpdate.getId());
        Assertions.assertNull(newEntity.getId());
        E savedEntityToUpdate = getRootContext().repoSave(entityToUpdate);
        newEntity.setId(savedEntityToUpdate.getId());
    }

    public SuccessfulUpdateTestConfiguration <E,Id> getDefaultSuccessfulContext(){
        return SuccessfulUpdateTestConfiguration.<E,Id>builder()
                .full(true)
                .repoEntityEqualChecker(getRootContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getRootContext().getDefaultEqualChecker())
                .build();
    }

    public FailedUpdateTestConfiguration<E,Id> getDefaultFailedContext(){
        return FailedUpdateTestConfiguration.<E,Id>builder()
                .full(true)
                .repoEntityEqualChecker(getDefaultPartialUpdateEqualChecker())
                .returnedEntityEqualChecker(getDefaultPartialUpdateEqualChecker())
                .build();
    }

}
