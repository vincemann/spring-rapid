package io.github.vincemann.demo.service.plugin;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudService_PluginProxy;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AclPlugin extends CrudService_PluginProxy.Plugin<IdentifiableEntity<Long>,Long> {


    @Override
    public void onBeforeSave(IdentifiableEntity<Long> entity) throws BadEntityException {
        super.onBeforeSave(entity);
        log.debug("creating acl list for Entity with class: " + getCrudService().getEntityClass());
    }

    @Override
    public void onAfterDelete(IdentifiableEntity<Long> requestEntity) throws NoIdException, EntityNotFoundException {
        super.onAfterDelete(requestEntity);
        log.debug("deleting acl list for Entity with class: " + getCrudService().getEntityClass());
    }

    @Override
    public void onAfterDeleteById(Long aLong) throws NoIdException, EntityNotFoundException {
        super.onAfterDeleteById(aLong);
        log.debug("deleting acl list for Entity with class: " + getCrudService().getEntityClass());
    }
}
