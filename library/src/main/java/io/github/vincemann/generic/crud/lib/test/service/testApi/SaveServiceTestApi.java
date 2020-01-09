package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.ServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.context.DefaultTestContext;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class SaveServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
    extends ServiceTestApi<E,Id,R>
{

    protected E saveEntity_ShouldSucceed(E entityToSave) throws BadEntityException {
        return saveEntity_ShouldSucceed(entityToSave, useDefaultContext());
    }

    protected E saveEntity_ShouldSucceed(E entityToSave, DefaultTestContext<E,Id> testContext) throws BadEntityException {
        //given
        Assertions.assertNull(entityToSave.getId());

        //when
        E savedTestEntity = serviceSave(entityToSave);

        //then
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());

        //if service save method does not copy entityToSave, then entitytoSaveRef = savedEntityFromServiceRef, otherwise not
        Optional<E> repoEntity = repoFindById(savedTestEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());

        Id oldId_EntityToSave = entityToSave.getId();
        entityToSave.setId(savedTestEntity.getId());

        Assertions.assertTrue(testContext.getRepoEntityEqualChecker().isEqual(entityToSave,repoEntity.get()));
        Assertions.assertTrue(testContext.getReturnedEntityEqualChecker().isEqual(entityToSave,savedTestEntity));

        //restore old id
        entityToSave.setId(oldId_EntityToSave);

        return savedTestEntity;
    }

    protected <T extends Throwable> T saveEntity_ShouldFail(E entityToSave, Class<? extends T> expectedException) {
        //when
        return Assertions.assertThrows(expectedException, () -> serviceSave(entityToSave));
    }

    private DefaultTestContext<E,Id> useDefaultContext(){
        return DefaultTestContext.<E,Id>builder()
                .repoEntityEqualChecker(getDefaultEqualChecker())
                .returnedEntityEqualChecker(getDefaultEqualChecker())
                .build();
    }
}
