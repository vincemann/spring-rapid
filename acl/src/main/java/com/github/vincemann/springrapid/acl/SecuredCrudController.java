package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;

public abstract class SecuredCrudController<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends CrudController<E,Id> {

    @Autowired
    @Secured
    @Lazy
    @Override
    public void setCrudService(CrudService<E,Id> crudService) {
        super.setCrudService(crudService);
    }


}
