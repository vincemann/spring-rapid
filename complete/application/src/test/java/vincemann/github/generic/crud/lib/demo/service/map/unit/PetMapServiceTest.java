package vincemann.github.generic.crud.lib.demo.service.map.unit;

import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.map.PetMapService;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

class PetMapServiceTest extends CrudServiceTest<PetMapService, Pet,Long> {

    @Override
    protected CrudServiceTestEntry<PetMapService, Pet,Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new PetMapService(), new Pet()
        );
    }
}