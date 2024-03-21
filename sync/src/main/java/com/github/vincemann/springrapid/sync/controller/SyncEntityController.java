package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.EntityController;
import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;
import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.service.SyncService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Offers methods for evaluating {@link EntitySyncStatus} of an entity or multiple entities.
 * Entities need to record audit information -> {@link AuditingEntity}.
 * Client can check if updates need to be done, and what kind of update is required, before actually fetching.
 *
 * @see EntitySyncStatus
 */
public abstract class SyncEntityController<E extends IAuditingEntity<Id>, Id extends Serializable,S extends SyncService<E,Id>>
        extends EntityController<E, Id>
        implements ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    private S service;

    private String fetchSyncStatusUrl;

    private String fetchSyncStatusesUrl;

    private String fetchSyncStatusesSinceTsUrl;

    private IdConverter<Id> idConverter;

    @SuppressWarnings("unchecked")
    public SyncEntityController() {
        super();
    }


    /**
     * used for single entity sync.
     * GET /api/core/entity/sync-status?id=42,ts=...
     * returns 200 if updated needed with json body of {@link EntitySyncStatus}.
     * or 204 if no update is needed
     */
    public ResponseEntity<String> fetchSyncStatus(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, JsonProcessingException {
        try {
            Id id = idConverter.toId(readRequestParam(request,"id"));
            log.debug(LogMessage.format("fetching entities sync status for entity with id: %s",id.toString()));
            long lastUpdateTimestamp = Long.parseLong(request.getParameter("ts"));
            log.debug(LogMessage.format("clients last update was at: %s",new Date(lastUpdateTimestamp).toString()));
            Timestamp lastUpdate = new Timestamp(lastUpdateTimestamp);
            VerifyEntity.isPresent(lastUpdateTimestamp, "need timestamp parameter 'ts'");
            LastFetchInfo lastFetchInfo = new LastFetchInfo(id.toString(), lastUpdate);
            EntitySyncStatus syncStatus = findEntitySyncStatus(lastFetchInfo);
            boolean updated = syncStatus != null;
            if (updated){
                log.debug(LogMessage.format("sync status of entity: %s",syncStatus.toString()));
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(syncStatus));
            } else{
                log.debug("sync status is: no update");
                return ResponseEntity.noContent().build();
            }
        } catch (NumberFormatException e) {
            throw new BadEntityException("Invalid timestamp format. Send unix timestamp long value.");
        }
    }

    /**
     * receives Set of {@link EntityUpdateInfo} of client in body and looks these through.
     * Returns client Set of {@link EntitySyncStatus} for those that need update with respective {@link EntitySyncStatus#getStatus()}.
     * <p>
     * If no updated required at all, returns 204 without body.
     *
     * POST /api/core/entity/sync-statuses
     *
     *
     */
    public ResponseEntity<String> syncEntities(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        try {
            String json = readBody(request);
            CollectionType idSetType = getObjectMapper()
                    .getTypeFactory().constructCollectionType(Set.class, LastFetchInfo.class);
            Set<LastFetchInfo> lastClientFetchInfos = getObjectMapper().readValue(json, idSetType);
            log.debug(LogMessage.format("fetch entities sync statuses request received, clients last fetch infos: %s",lastClientFetchInfos.stream().map(LastFetchInfo::toString).collect(Collectors.toSet())));

            List<EntitySyncStatus> syncStatuses = findEntitySyncStatuses(lastClientFetchInfos);
            log.debug(LogMessage.format("sync statuses of requested entities: %s",syncStatuses.stream().map(EntitySyncStatus::toString).collect(Collectors.toSet())));
            if (syncStatuses.isEmpty())
                return ResponseEntity.noContent().build();
            else
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(syncStatuses));
        } catch (IOException e) {
            throw new BadEntityException("invalid format for EntityLastUpdateInfo. Use json list.");
        }
    }

    /**
     * client passes timestamp, of when last update for find-all (with potential filter) was performed, to server.
     * Client can pass list of filters bean names, that should be applied in that order.
     * <p>
     * Server returns Set of {@link EntitySyncStatus} of all entities, that have been removed, added or updated since then.
     * <p>
     * GET /api/core/entity/sync-statuses-since?ts=...
     *
     */
    public ResponseEntity<String> sync(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, JsonProcessingException {
        long lastUpdateTimestamp = Long.parseLong(request.getParameter("ts"));

        log.debug(LogMessage.format("find sync statuses since timestamp request received. Timestamp: %s",new Date(lastUpdateTimestamp).toString()));

        List<EntitySyncStatus> syncStatuses = findUpdatesSinceTimestamp(new Timestamp(lastUpdateTimestamp));
        if (syncStatuses.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(syncStatuses));
    }


    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        if (!getIgnoredEndPoints().contains(getFetchSyncStatusUrl()))
            registerEndpoint(createFetchEntitySyncStatusRequestMappingInfo(), "fetchEntitySyncStatus");
        if (!getIgnoredEndPoints().contains(getFetchSyncStatusesUrl()))
            registerEndpoint(createFetchEntitySyncStatusesRequestMappingInfo(), "fetchEntitySyncStatuses");
        if (!getIgnoredEndPoints().contains(getFetchSyncStatusesSinceTsUrl()))
            registerEndpoint(createFetchEntitySyncStatusesSinceTsRequestMappingInfo(), "fetchEntitySyncStatusesSinceTimestamp");
    }


    protected EntitySyncStatus findEntitySyncStatus(LastFetchInfo clientLastFetch) throws EntityNotFoundException {
        return service.findEntitySyncStatus(clientLastFetch);
    }

    protected List<EntitySyncStatus> findEntitySyncStatuses(Set<LastFetchInfo> lastFetchInfos) throws EntityNotFoundException {
        return service.findEntitySyncStatuses(lastFetchInfos);
    }

    protected List<EntitySyncStatus> findUpdatesSinceTimestamp(Timestamp lastUpdate) {
        return service.findEntitySyncStatusesSinceTimestamp(lastUpdate);
    }

    protected RequestMappingInfo createFetchEntitySyncStatusRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchSyncStatusUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesSinceTsRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchSyncStatusesSinceTsUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesRequestMappingInfo() {
        return RequestMappingInfo
                .paths(fetchSyncStatusesUrl)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected void initUrls() {
        super.initUrls();
        this.fetchSyncStatusUrl = entityBaseUrl + "sync-status";
        this.fetchSyncStatusesUrl = entityBaseUrl + "sync-statuses";
        this.fetchSyncStatusesSinceTsUrl = entityBaseUrl + "sync-statuses-since";
    }


    public S getService() {
        return service;
    }

    public String getFetchSyncStatusUrl() {
        return fetchSyncStatusUrl;
    }

    public String getFetchSyncStatusesUrl() {
        return fetchSyncStatusesUrl;
    }

    public String getFetchSyncStatusesSinceTsUrl() {
        return fetchSyncStatusesSinceTsUrl;
    }

    public void setFetchSyncStatusUrl(String fetchSyncStatusUrl) {
        this.fetchSyncStatusUrl = fetchSyncStatusUrl;
    }

    public void setFetchSyncStatusesUrl(String fetchSyncStatusesUrl) {
        this.fetchSyncStatusesUrl = fetchSyncStatusesUrl;
    }

    public void setFetchSyncStatusesSinceTsUrl(String fetchSyncStatusesSinceTsUrl) {
        this.fetchSyncStatusesSinceTsUrl = fetchSyncStatusesSinceTsUrl;
    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Autowired
    public void setService(S service) {
        this.service = service;
    }
}
