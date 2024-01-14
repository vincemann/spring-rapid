package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.AbstractEntityController;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.service.SyncService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
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
import java.util.List;
import java.util.Set;

/**
 * Offers methods for evaluating {@link EntitySyncStatus} of an entity or multiple entities.
 * Entities need to record audit information -> {@link AuditingEntity}.
 * Client can check if updates need to be done, and what kind of update is required, before actually fetching.
 *
 * @see EntitySyncStatus
 */
@Slf4j
@Getter
public class SyncEntityController<
        E extends AuditingEntity<ID>,
        ID extends Serializable,
        S extends SyncService<E, ID>>
        extends AbstractEntityController<E, ID>
        implements ApplicationContextAware {

    private IdFetchingStrategy<ID> idFetchingStrategy;
    private S service;
    @Setter
    private String fetchEntitySyncStatusUrl;
    @Setter
    private String fetchEntitySyncStatusesUrl;
    @Setter
    private String fetchEntitySyncStatusesSinceTsUrl;

    private EntitySyncStatusSerializer entitySyncStatusSerializer;

    @SuppressWarnings("unchecked")
    public SyncEntityController() {
        super();
    }


    /**
     * used for single entity sync.
     * GET /api/core/entity/fetch-entity-sync-status?id=42,ts=...
     * returns 200 if updated needed with body {@link EntitySyncStatusSerializer#serialize(EntitySyncStatus)}
     * or 204 if no update is needed
     */
    public ResponseEntity<String> fetchEntitySyncStatus(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        try {
            ID id = fetchId(request);
            long lastUpdateTimestamp = Long.parseLong(request.getParameter("ts"));
            // jpa uses this format
            //            Date lastUpdateDate = DATE_FORMAT.parse(lastUpdateTimestampString);
            Timestamp lastUpdate = new Timestamp(lastUpdateTimestamp);
            VerifyEntity.isPresent(lastUpdateTimestamp, "need timestamp parameter 'ts'");
            EntitySyncStatus syncStatus = serviceFindEntitySyncStatus(new EntityLastUpdateInfo(id.toString(), lastUpdate));
            boolean updated = syncStatus != null;
            if (updated)
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(entitySyncStatusSerializer.serialize(syncStatus));
            else
                return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            throw new BadEntityException("Invalid timestamp format. Send unix timestamp long value.");
        }
    }

    /**
     * receives Set of {@link EntityLastUpdateInfo} of client in body and looks these through.
     * Returns client Set of {@link EntitySyncStatus} for those that need update with respective {@link EntitySyncStatus#getStatus()}.
     * <p>
     * If no updated required at all, returns 204 without body.
     *
     * POST /api/core/entity/fetch-entity-sync-statuses
     *
     *
     */
    public ResponseEntity<String> fetchEntitySyncStatuses(HttpServletRequest request, HttpServletResponse response) throws BadEntityException {
        try {
            String json = readBody(request);
            CollectionType idSetType = getJsonMapper().getObjectMapper()
                    .getTypeFactory().constructCollectionType(Set.class, EntityLastUpdateInfo.class);
            Set<EntityLastUpdateInfo> lastUpdateInfos = getJsonMapper().readDto(json, idSetType);
//            List<JPQLEntityFilter<E>> filters = HttpServletRequestUtils.extractFilters(request,applicationContext,"jpql-filter");


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

    /**
     * client passes timestamp, of when last update for find-all (with potential filter) was performed, to server.
     * searched entity space can be reduced with {@link EntityFilter}s.
     * Client can pass list of filters bean names, that should be applied in that order.
     * <p>
     * Server returns Set of {@link EntitySyncStatus} of all entities, that have been removed, added or updated since then.
     * <p>
     * GET /api/core/entity/fetch-entity-sync-statuses-since-ts?ts=...&jpql-filter=filter1:arg1:arg2,filter2
     *
     * Filters are optional and only {@link QueryFilter} is supported.
     */
    public ResponseEntity<String> fetchEntitySyncStatusesSinceTimestamp(HttpServletRequest request, HttpServletResponse response) throws BadEntityException {
        // todo maybe add allowed list of filters hardcoded for more security
        // what about default filters? rather integrate as ServiceExtensions?
        // what about JPQL where clause filters for performance or smth like that
        long lastUpdateTimestamp = Long.parseLong(request.getParameter("ts"));
        List<QueryFilter<E>> filters = extractArgAwareExtension(request,QUERY_FILTER_URL_KEY);
        Set<EntitySyncStatus> syncStatuses = serviceFindUpdatesSinceTimestamp(new Timestamp(lastUpdateTimestamp),filters);
        if (syncStatuses.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(entitySyncStatusSerializer.serialize(syncStatuses));
    }


    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        registerEndpoint(createFetchEntitySyncStatusRequestMappingInfo(), "fetchEntitySyncStatus");
        registerEndpoint(createFetchEntitySyncStatusesRequestMappingInfo(), "fetchEntitySyncStatuses");
        registerEndpoint(createFetchEntitySyncStatusesSinceTsRequestMappingInfo(), "fetchEntitySyncStatusesSinceTimestamp");
    }


    protected EntitySyncStatus serviceFindEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) {
        return service.findEntitySyncStatus(lastUpdateInfo);
    }

    protected Set<EntitySyncStatus> serviceFindEntitySyncStatuses(Set<EntityLastUpdateInfo> lastUpdateInfos) {
        return service.findEntitySyncStatuses(lastUpdateInfos);
    }

    protected Set<EntitySyncStatus> serviceFindUpdatesSinceTimestamp(Timestamp lastUpdate, List<QueryFilter<E>> filters) {
        return service.findEntitySyncStatusesSinceTimestamp(lastUpdate,filters);
    }

    protected RequestMappingInfo createFetchEntitySyncStatusRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchEntitySyncStatusUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesSinceTsRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchEntitySyncStatusesSinceTsUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesRequestMappingInfo() {
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
        this.fetchEntitySyncStatusesSinceTsUrl = entityBaseUrl + "fetch-entity-sync-statuses-since-ts";
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
    public void injectEntitySyncStatusSerializer(EntitySyncStatusSerializer entitySyncStatusSerializer) {
        this.entitySyncStatusSerializer = entitySyncStatusSerializer;
    }
}
