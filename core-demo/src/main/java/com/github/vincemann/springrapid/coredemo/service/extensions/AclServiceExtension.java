package com.github.vincemann.springrapid.coredemo.service.extensions;

import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.Serializable;

/**
 * Example Demo Extension that can be plugged in to all {@link CrudService}s.
 */
@Slf4j
@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AclServiceExtension
        extends BasicServiceExtension<CrudService>
            implements CrudServiceExtension<CrudService> {


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        log.debug("creating acl list for Entity with class: " + getEntityClass());
        return getNext().save(entity);
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        log.debug("deleting acl list for Entity with class: " + getEntityClass());
        getNext().deleteById(id);
    }


}
