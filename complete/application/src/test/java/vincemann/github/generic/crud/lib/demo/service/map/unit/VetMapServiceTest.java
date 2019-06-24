package vincemann.github.generic.crud.lib.demo.service.map.unit;

import vincemann.github.generic.crud.lib.demo.model.Vet;
import vincemann.github.generic.crud.lib.demo.service.map.SpecialtyMapService;
import vincemann.github.generic.crud.lib.demo.service.map.VetMapService;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

class VetMapServiceTest extends CrudServiceTest<VetMapService,Vet,Long> {

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<VetMapService, Vet, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new VetMapService(new SpecialtyMapService()), new Vet()
        );
    }
}