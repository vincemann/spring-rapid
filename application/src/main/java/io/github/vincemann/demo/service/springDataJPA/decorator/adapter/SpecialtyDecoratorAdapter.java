package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.CrudServiceDecoratorAdapter;
import lombok.Builder;

public class SpecialtyDecoratorAdapter extends CrudServiceDecoratorAdapter<Specialty,Long, SpecialtyService> implements SpecialtyService {

    @Builder
    public SpecialtyDecoratorAdapter(SpecialtyService undecoratedService, CrudService<Specialty, Long> crudServiceDecorator) {
        super(undecoratedService, crudServiceDecorator);
    }
}
