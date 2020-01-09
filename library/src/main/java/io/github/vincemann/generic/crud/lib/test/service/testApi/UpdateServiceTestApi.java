package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.AbstractServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.RootServiceTestContext;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class UpdateServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
        extends AbstractServiceTestApi<E,Id,R>
{
    public static final String PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER = "partialUpdateEqualCheckerBean";
    private EqualChecker<E> partialUpdateEqualChecker;

    public UpdateServiceTestApi(RootServiceTestContext<E, Id, R> serviceTestContext) {
        super(serviceTestContext);
    }

    @Getter
    public abstract class TestContext{
        private boolean fullUpdate;
        private PostUpdateCallback<E, Id> postUpdateCallback;
        private EqualChecker<E> repoEntityEqualChecker;
        private EqualChecker<E> returnedEntityEqualChecker;

        public TestContext(boolean fullUpdate, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
            this.fullUpdate = fullUpdate;
            this.postUpdateCallback = postUpdateCallback;
            this.repoEntityEqualChecker = repoEntityEqualChecker;
            this.returnedEntityEqualChecker = returnedEntityEqualChecker;
        }
    }

    @Getter
    public class SuccessfulTestContext extends TestContext{

        @Builder(builderMethodName = "fullUpdateContextBuilder")
        public SuccessfulTestContext(PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
            super(true,postUpdateCallback,repoEntityEqualChecker,returnedEntityEqualChecker);
        }


        @Builder(builderMethodName = "partialUpdateContextBuilder")
        public SuccessfulTestContext(PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> partialUpdateEqualChecker) {
            super(false, postUpdateCallback, partialUpdateEqualChecker,partialUpdateEqualChecker);
        }
    }


    @Getter
    public class FailedTestContext<T extends Throwable> extends TestContext{
        private Class<? extends T> expectedException;
        private EqualChecker<E> equalChecker;

        @Builder(builderMethodName = "partialUpdateContextBuilder")
        public FailedTestContext(PostUpdateCallback<E, Id> postUpdateCallback, Class<? extends T> expectedException, EqualChecker<E> equalChecker) {
            super(false, postUpdateCallback,equalChecker,equalChecker);
            this.expectedException = expectedException;
            this.equalChecker = equalChecker;
        }

        @Builder(builderMethodName = "fullUpdateContextBuilder")
        public FailedTestContext(PostUpdateCallback<E, Id> postUpdateCallback) {
            super(true, postUpdateCallback,partialUpdateEqualChecker,partialUpdateEqualChecker);
        }
    }

    @Autowired
    @Qualifier(PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER)
    protected void injectPartialUpdateEqualChecker(EqualChecker<E> partialUpdateEqualChecker){
        this.partialUpdateEqualChecker=partialUpdateEqualChecker;
    }


    public E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity, SuccessfulTestContext successfulTestContext) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity, successfulTestContext);
    }

    public E updateEntity_ShouldSucceed(E newEntity, SuccessfulTestContext successfulTestContext) throws NoIdException, BadEntityException, EntityNotFoundException {
        //given
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(successfulTestContext.getRepoEntityEqualChecker().isEqual(newEntity,entityToUpdate.get()));

        //when
        E updatedEntity = getRootContext().serviceUpdate(newEntity, successfulTestContext.isFullUpdate());

        //todo change equal checker for non full update to only compare values in request entity that are not null
        //then
        Assertions.assertEquals(updatedEntity.getId(),newEntity.getId());
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(updatedEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());


        Assertions.assertTrue(successfulTestContext.getRepoEntityEqualChecker().isEqual(newEntity,updatedEntity));
        Optional<E> updatedEntityFromRepo = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedEntityFromRepo.isPresent());
        Assertions.assertTrue(successfulTestContext.getReturnedEntityEqualChecker().isEqual(newEntity,updatedEntity));

        return updatedEntity;
    }



    public <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity) throws NoIdException {
        return (T) updateExistingEntity_ShouldFail(newEntity,getDefaultFailedContext().build());
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, FailedTestContext<T> failedTestContext) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateExistingEntity_ShouldFail(newEntity,failedTestContext);
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return (T) updateExistingEntity_ShouldFail(newEntity,getDefaultFailedContext().build());
    }

    public <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity,  FailedTestContext<T> testContext) throws NoIdException{
        //given
        //entity to update is present
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(testContext.getEqualChecker().isEqual(newEntity,entityToUpdate.get()));

        //when
        T exception = Assertions.assertThrows(testContext.getExpectedException(), () -> getRootContext().serviceUpdate(newEntity,testContext.isFullUpdate()));

        //then
        Optional<E> updatedRepoEntity = getRootContext().repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        //still the same
        Assertions.assertTrue(testContext.getEqualChecker().isEqual(entityToUpdate.get(),updatedRepoEntity.get()));
        return exception;
    }

    private void saveEntityForUpdate(E entityToUpdate, E newEntity) {
        Assertions.assertNull(entityToUpdate.getId());
        Assertions.assertNull(newEntity.getId());
        E savedEntityToUpdate = getRootContext().repoSave(entityToUpdate);
        newEntity.setId(savedEntityToUpdate.getId());
    }

    public SuccessfulTestContext.SuccessfulTestContextBuilder getDefaultSuccessfulContext(){
        return SuccessfulTestContext.fullUpdateContextBuilder();
    }

    public <T extends Throwable> FailedTestContext.FailedTestContextBuilder<T> getDefaultFailedContext(){
        return FailedTestContext.<T>fullUpdateContextBuilder();
    }

}
