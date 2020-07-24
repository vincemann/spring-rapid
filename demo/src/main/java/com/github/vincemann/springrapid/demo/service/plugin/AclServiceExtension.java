package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.io.Serializable;

@Slf4j
@ServiceComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AclServiceExtension extends ServiceExtension<SimpleCrudService>
        implements SimpleCrudServiceExtension<SimpleCrudService> {

    public AclServiceExtension() {
        log.debug("creating acl service extension: " + this);
    }

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
