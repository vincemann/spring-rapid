package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public abstract class SecuredCrudController
        <
                E extends IdAwareEntity<Id>,
                Id extends Serializable,
                S extends CrudService<E,Id>
                >
        extends CrudController<E,Id,S> {

    @Autowired
    @Secured
    @Override
    public void setCrudService(S crudService) {
        super.setCrudService(crudService);
    }


}
