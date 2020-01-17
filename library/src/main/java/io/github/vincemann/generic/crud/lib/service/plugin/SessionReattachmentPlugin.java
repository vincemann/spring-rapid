package io.github.vincemann.generic.crud.lib.service.plugin;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraphSessionReattacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class SessionReattachmentPlugin
    //todo why doesnt this work with Serializable instead of Long?
        extends CrudServicePluginProxy.Plugin<IdentifiableEntity<Long>,Long> {

    private EntityGraphSessionReattacher entityGraph_sessionReattacher;

    @Autowired
    public SessionReattachmentPlugin(EntityGraphSessionReattacher entityGraph_sessionReattacher) {
        this.entityGraph_sessionReattacher = entityGraph_sessionReattacher;
    }

    @Transactional
    @Override
    public void onBeforeSave(IdentifiableEntity entity) throws BadEntityException {
        log.debug("attaching entityGraph to Session if necessary. Root: " + entity);
        entityGraph_sessionReattacher.attachEntityGraphToCurrentSession(entity);
        super.onBeforeSave(entity);
    }


}
