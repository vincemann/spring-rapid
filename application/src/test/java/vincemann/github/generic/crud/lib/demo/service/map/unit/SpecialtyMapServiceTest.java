package vincemann.github.generic.crud.lib.demo.service.map.unit;

import vincemann.github.generic.crud.lib.demo.model.Specialty;
import vincemann.github.generic.crud.lib.demo.service.map.SpecialtyMapService;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

class SpecialtyMapServiceTest extends CrudServiceTest<SpecialtyMapService,Specialty,Long> {

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<SpecialtyMapService, Specialty, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new SpecialtyMapService(), new Specialty());
    }
}