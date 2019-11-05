package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.CrudServiceDecoratorAdapter;
import lombok.Builder;

import java.util.Optional;

public class OwnerServiceDecoratorAdapter extends CrudServiceDecoratorAdapter<Owner,Long,OwnerService> implements OwnerService{

    @Builder
    public OwnerServiceDecoratorAdapter(OwnerService ownerService, CrudService<Owner, Long> crudServiceDecorator) {
        super(ownerService, crudServiceDecorator);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getUndecoratedService().findByLastName(lastName);
    }
}
