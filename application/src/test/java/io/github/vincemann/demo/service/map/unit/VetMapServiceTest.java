package io.github.vincemann.demo.service.map.unit;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.map.SpecialtyMapService;
import io.github.vincemann.demo.service.map.VetMapService;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

class VetMapServiceTest extends CrudServiceTest<VetMapService, Vet,Long> {

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<VetMapService, Vet, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new VetMapService(new SpecialtyMapService()), new Vet()
        );
    }
}