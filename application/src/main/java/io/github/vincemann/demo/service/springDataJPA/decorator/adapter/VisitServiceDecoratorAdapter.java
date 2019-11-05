package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.demo.model.Visit;
import io.github.vincemann.demo.service.VisitService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.CrudServiceDecoratorAdapter;
import lombok.Builder;

public class VisitServiceDecoratorAdapter extends CrudServiceDecoratorAdapter<Visit,Long, VisitService> implements VisitService {

    @Builder
    public VisitServiceDecoratorAdapter(VisitService undecoratedService, CrudService<Visit, Long> crudServiceDecorator) {
        super(undecoratedService, crudServiceDecorator);
    }
}
