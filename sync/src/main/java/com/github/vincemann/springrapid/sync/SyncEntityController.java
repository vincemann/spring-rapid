package com.github.vincemann.springrapid.sync;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.AbstractEntityController;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.sync.dto.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.dto.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.serialize.EntitySyncStatusSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Offers methods for evaluating {@link EntitySyncStatus} of an entity or multiple entities.
 * Entities need to record audit information -> {@link AuditingEntity}.
 * Client can check if updates need to be done, and what kind of update is required, before actually fetching.
 * @see EntitySyncStatus
 */
@Slf4j
@Getter
public class SyncEntityController<
        E extends AuditingEntity<ID>,
        ID extends Serializable,
        S extends AuditingService<ID>>
        extends AbstractEntityController<E, ID> {

    private IdFetchingStrategy<ID> idFetchingStrategy;
    private S service;
    @Setter
    private String fetchEntitySyncStatusUrl;
    @Setter
    private String fetchEntitySyncStatusesUrl;

    private EntitySyncStatusSerializer entitySyncStatusSerializer;

    @SuppressWarnings("unchecked")
    public SyncEntityController() {
        super();
    }


    /**
     * used for single entity sync.
     * GET /api/core/entity/fetch-entity-sync-status?id=42,last-update-ts=...
     * returns 200 if updated needed with body {@link EntitySyncStatusSerializer#serialize(EntitySyncStatus)}
     * or 204 if no update is needed
     */
    public ResponseEntity<String> fetchEntitySyncStatus(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        try {
            ID id = fetchId(request);
            long lastUpdateTimestamp = Long.parseLong(request.getParameter("last-update-ts"));
            // jpa uses this format
    //            Date lastUpdateDate = DATE_FORMAT.parse(lastUpdateTimestampString);
            Timestamp lastUpdate = new Timestamp(lastUpdateTimestamp);
            VerifyEntity.isPresent(lastUpdateTimestamp, "need 'last-update-ts' parameter");
            EntitySyncStatus syncStatus = serviceFindEntitySyncStatus(new EntityLastUpdateInfo(id.toString(), lastUpdate));
            boolean updated = syncStatus != null;
            if (updated)
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(entitySyncStatusSerializer.serialize(syncStatus));
            else
                return ResponseEntity.noContent().build();
        }catch (NumberFormatException e) {
            throw new BadEntityException("Invalid timestamp format. Send unix timestamp long value.");
        }
    }

    /**
     * receives Set of {@link com.github.vincemann.springrapid.sync.dto.EntityLastUpdateInfo} of client and looks these through.
     * Returns client Set of {@link EntitySyncStatus} for those that need update with respective {@link EntitySyncStatus#getStatus()}.
     *
     * If no updated required at all, returns 204 without body.
     */
    public ResponseEntity<String> fetchEntitySyncStatuses(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        try {
            String json = readBody(request);
            CollectionType idSetType = getJsonMapper().getObjectMapper()
                    .getTypeFactory().constructCollectionType(Set.class, EntityLastUpdateInfo.class);
            Set<EntityLastUpdateInfo> lastUpdateInfos = getJsonMapper().readDto(json, idSetType);

            Set<EntitySyncStatus> syncStatuses = serviceFindEntitySyncStatuses(lastUpdateInfos);
            if (syncStatuses.isEmpty())
                return ResponseEntity.noContent().build();
            else
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(entitySyncStatusSerializer.serialize(syncStatuses));
        } catch (IOException e) {
            throw new BadEntityException("invalid format for EntityLastUpdateInfo. Use json list.");
        }
    }

    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        registerEndpoint(createFetchEntitySyncStatusRequestMappingInfo(), "fetchEntitySyncStatus");
        registerEndpoint(createFetchEntitySyncStatusesRequestMappingInfo(), "fetchEntitySyncStatuses");
    }


    protected EntitySyncStatus serviceFindEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) throws EntityNotFoundException {
        return service.findEntitySyncStatus(lastUpdateInfo);
    }

    protected Set<EntitySyncStatus> serviceFindEntitySyncStatuses(Set<EntityLastUpdateInfo> lastUpdateInfos) throws EntityNotFoundException {
        return service.findEntitiesSyncStatus(lastUpdateInfos);
    }

    private RequestMappingInfo createFetchEntitySyncStatusRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchEntitySyncStatusUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    private RequestMappingInfo createFetchEntitySyncStatusesRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchEntitySyncStatusesUrl)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    protected void initUrls() {
        super.initUrls();
        this.fetchEntitySyncStatusUrl = entityBaseUrl + "fetch-entity-sync-status";
        this.fetchEntitySyncStatusesUrl = entityBaseUrl + "fetch-entity-sync-statuses";
    }


    protected ID fetchId(HttpServletRequest request) throws IdFetchingException {
        return this.getIdFetchingStrategy().fetchId(request);
    }

    @Autowired
    public void injectIdFetchingStrategy(IdFetchingStrategy<ID> idFetchingStrategy) {
        this.idFetchingStrategy = idFetchingStrategy;
    }

    @Autowired
    @Lazy
    public void injectService(S service) {
        this.service = service;
    }

    @Autowired
    public void setEntitySyncStatusSerializer(EntitySyncStatusSerializer entitySyncStatusSerializer) {
        this.entitySyncStatusSerializer = entitySyncStatusSerializer;
    }
}
