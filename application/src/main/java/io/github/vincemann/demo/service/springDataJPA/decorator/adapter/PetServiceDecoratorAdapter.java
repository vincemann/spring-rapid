package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.CrudServiceDecoratorAdapter;
import lombok.Builder;

public class PetServiceDecoratorAdapter extends CrudServiceDecoratorAdapter<Pet,Long, PetService> implements PetService{

    @Builder
    public PetServiceDecoratorAdapter(PetService undecoratedService, CrudService<Pet, Long> crudServiceDecorator) {
        super(undecoratedService, crudServiceDecorator);
    }
}
