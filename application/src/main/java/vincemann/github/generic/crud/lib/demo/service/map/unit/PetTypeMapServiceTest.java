package vincemann.github.generic.crud.lib.demo.service.map.unit;

import vincemann.github.generic.crud.lib.demo.model.PetType;

import vincemann.github.generic.crud.lib.demo.service.map.PetTypeMapService;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

class PetTypeMapServiceTest extends CrudServiceTest<PetTypeMapService, PetType,Long> {

    @Override
    protected CrudServiceTestEntry<PetTypeMapService, PetType, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new PetTypeMapService(), new PetType()
        );
    }
}