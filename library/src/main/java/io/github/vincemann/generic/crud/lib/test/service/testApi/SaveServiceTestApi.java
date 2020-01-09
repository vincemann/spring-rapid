package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.AbstractServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class SaveServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
        extends AbstractServiceTestApi<E,Id,R>
{
    @Getter
    public class TestContext{
        private EqualChecker<E> repoEntityEqualChecker;
        private EqualChecker<E> returnedEntityEqualChecker;

        @Builder
        public TestContext(EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
            this.repoEntityEqualChecker = repoEntityEqualChecker;
            this.returnedEntityEqualChecker = returnedEntityEqualChecker;
        }
    }

    public SaveServiceTestApi(RootServiceTestContext<E, Id, R> serviceTestContext) {
        super(serviceTestContext);
    }

    public E saveEntity_ShouldSucceed(E entityToSave) throws BadEntityException {
        return saveEntity_ShouldSucceed(entityToSave, getDefaultContext());
    }

    public E saveEntity_ShouldSucceed(E entityToSave, TestContext testContext) throws BadEntityException {
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

        Assertions.assertTrue(testContext.getRepoEntityEqualChecker().isEqual(entityToSave,repoEntity.get()));
        Assertions.assertTrue(testContext.getReturnedEntityEqualChecker().isEqual(entityToSave,savedTestEntity));

        //restore old id
        entityToSave.setId(oldId_EntityToSave);

        return savedTestEntity;
    }

    public <T extends Throwable> T saveEntity_ShouldFail(E entityToSave, Class<? extends T> expectedException) {
        //when
        return Assertions.assertThrows(expectedException, () -> getRootContext().serviceSave(entityToSave));
    }

    public TestContext getDefaultContext(){
        return TestContext.builder()
                .repoEntityEqualChecker(getRootContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getRootContext().getDefaultEqualChecker())
                .build();
    }
}
