package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;

public abstract class SecuredRapidController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                S extends AbstractCrudService<E, Id, ?>
         >
        extends RapidController<E,Id,S> {

    @Secured
    @Lazy
    @Override
    public void injectCrudService(S crudService) {
        super.injectCrudService(crudService);
    }


}
