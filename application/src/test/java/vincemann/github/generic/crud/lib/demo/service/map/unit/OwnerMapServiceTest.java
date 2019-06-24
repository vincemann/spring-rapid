package vincemann.github.generic.crud.lib.demo.service.map.unit;

import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.service.map.OwnerMapService;
import vincemann.github.generic.crud.lib.demo.service.map.PetMapService;
import vincemann.github.generic.crud.lib.demo.service.map.PetTypeMapService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

import java.util.Optional;

class OwnerMapServiceTest extends CrudServiceTest<OwnerMapService,Owner,Long> {

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