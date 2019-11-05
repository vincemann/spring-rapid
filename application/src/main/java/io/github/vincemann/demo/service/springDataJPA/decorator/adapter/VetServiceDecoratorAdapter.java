package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.CrudServiceDecoratorAdapter;
import lombok.Builder;

public class VetServiceDecoratorAdapter extends CrudServiceDecoratorAdapter<Vet,Long, VetService> implements VetService{

    @Builder
    public VetServiceDecoratorAdapter(VetService undecoratedService, CrudService<Vet, Long> crudServiceDecorator) {
        super(undecoratedService, crudServiceDecorator);
    }
}
