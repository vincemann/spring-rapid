package io.github.vincemann.demo.service.map.unit;

import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.map.SpecialtyMapService;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

class SpecialtyMapServiceTest extends CrudServiceTest<SpecialtyMapService, Specialty,Long> {

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<SpecialtyMapService, Specialty, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new SpecialtyMapService(), new Specialty());
    }
}