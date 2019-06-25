package io.github.vincemann.demo.service.map.unit;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.map.OwnerMapService;
import io.github.vincemann.demo.service.map.PetMapService;
import io.github.vincemann.demo.service.map.PetTypeMapService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

import java.util.Optional;

class OwnerMapServiceTest extends CrudServiceTest<OwnerMapService, Owner,Long> {

    @Override
    protected CrudServiceTestEntry<OwnerMapService, Owner,Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new OwnerMapService(new PetTypeMapService(),new PetMapService()), new Owner());
    }

    @Test
    void findByLastNameTest() throws BadEntityException {
        String lastName = "Peter";
        Owner testOwner = new Owner();
        testOwner.setLastName(lastName);
        Owner savedPeter = saveEntity(testOwner);
        Optional<Owner> foundPeter = getCrudServiceTestEntry().getCrudService().findByLastName(lastName);
        Assertions.assertTrue(foundPeter.isPresent());
        Assertions.assertEquals(savedPeter,foundPeter.get());
    }
}