package io.github.vincemann.demo.service.plugin;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class AclPlugin extends CrudServicePlugin<IdentifiableEntity<Long>,Long> {


    public void onBeforeSave(IdentifiableEntity<Long> entity) throws BadEntityException {
        log.debug("creating acl list for Entity with class: " + getService().getEntityClass());
    }

    public void onAfterDelete(IdentifiableEntity<Long> requestEntity) throws NoIdException, EntityNotFoundException {
        log.debug("deleting acl list for Entity with class: " + getService().getEntityClass());
    }

    public void onAfterDeleteById(Long aLong) throws NoIdException, EntityNotFoundException {
        log.debug("deleting acl list for Entity with class: " + getService().getEntityClass());
    }
}
