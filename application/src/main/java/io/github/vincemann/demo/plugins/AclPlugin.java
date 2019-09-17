package io.github.vincemann.demo.plugins;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.plugins.AbstractDtoCrudControllerSpringAdapterPlugin;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AclPlugin extends AbstractDtoCrudControllerSpringAdapterPlugin<IdentifiableEntity<Long>,IdentifiableEntity<Long>,Long> {


    @Override
    public void beforeCreateEntity(IdentifiableEntity<Long> entity) {
        log.debug("creating acl list for Entity with class: " + getController().getServiceEntityClass());
        /* implement some acl logic here for example and plug this plugins in to all controllers that need acls*/
        /* code for creating acl list would be in this method for example*/
        super.beforeCreateEntity(entity);
    }

    @Override
    public void afterDeleteEntity(Long aLong) {
        log.debug("deleting acl list for Entity with class: " + getController().getServiceEntityClass());
        /*acl would be deleted here*/
        super.afterDeleteEntity(aLong);
    }
}
