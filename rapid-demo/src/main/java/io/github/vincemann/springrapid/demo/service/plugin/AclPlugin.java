package io.github.vincemann.springrapid.demo.service.plugin;

import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServiceComponent
public class AclPlugin extends CrudServicePlugin<IdentifiableEntity<Long>,Long> {

    public void onBeforeSave(IdentifiableEntity<Long> toSave, Class<? extends IdentifiableEntity<Long>> entityClass) {
        log.debug("creating acl list for Entity with class: " + entityClass);
    }

    public void onBeforeDeleteById(Long id, Class<? extends IdentifiableEntity<Long>> entityClass) {
        log.debug("deleting acl list for Entity with class: " + entityClass);
    }
}
