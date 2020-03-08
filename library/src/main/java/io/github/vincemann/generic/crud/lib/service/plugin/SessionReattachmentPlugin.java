package io.github.vincemann.generic.crud.lib.service.plugin;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraphSessionReattacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
/**
 * This Plugin prevents detached Entity persistence errors.
 * Use it for crud-services that manage Entities with children or parents, if you intend to save or update
 * entities with parents/children set as member variables, that were persisted in a different session (thus are detached).
 * This Plugin will automatically reattach all detached entities to the current session.
 */
public class SessionReattachmentPlugin
    //todo why doesnt this work with Serializable instead of Long?
        extends CrudServicePlugin<IdentifiableEntity<Long>,Long> {

    private EntityGraphSessionReattacher entityGraph_sessionReattacher;

    public SessionReattachmentPlugin(EntityGraphSessionReattacher entityGraph_sessionReattacher) {
        this.entityGraph_sessionReattacher = entityGraph_sessionReattacher;
    }

    public void onBeforeSave(IdentifiableEntity<Long> entity) throws BadEntityException {
        log.debug("attaching entityGraph to Session if necessary. Root: " + entity);
        entityGraph_sessionReattacher.attachEntityGraphToCurrentSession(entity);
    }


}
