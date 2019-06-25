package io.github.vincemann.demo.service.map.unit;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.map.PetTypeMapService;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

class PetTypeMapServiceTest extends CrudServiceTest<PetTypeMapService, PetType,Long> {

    @Override
    protected CrudServiceTestEntry<PetTypeMapService, PetType, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new PetTypeMapService(), new PetType()
        );
    }
}