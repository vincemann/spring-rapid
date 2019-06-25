package io.github.vincemann.demo.service.map.unit;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.map.PetMapService;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

class PetMapServiceTest extends CrudServiceTest<PetMapService, Pet,Long> {

    @Override
    protected CrudServiceTestEntry<PetMapService, Pet,Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new PetMapService(), new Pet()
        );
    }
}