package com.github.vincemann.springrapid.sync.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.AbstractController;
import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
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
 * Offers endpoints for evaluating {@link EntitySyncStatus} of an entity or multiple entities.
 * Entities need to record audit information -> must extend {@link AuditingEntity} or implement {@link com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity}.
 * Client can check if updates need to be fetched, and what kind of update is required, before actually fetching.
 *
 * @see EntitySyncStatus
 * @see SyncService
 */
public abstract class SyncEntityController<E extends AuditingEntity<?>,S extends SyncService<E,?>>
        extends AbstractController
        implements InitializingBean {

    private final Log log = LogFactory.getLog(getClass());

    private S service;

    private String syncEntityUrl;

    private String syncEntitiesUrl;

    private String syncEntitiesSinceUrl;

    private IdConverter idConverter;


    @SuppressWarnings("unchecked")
    public SyncEntityController() {
        super();
    }


    /**
     * Endpoint for single entity sync.
     * GET /api/core/entity/sync?id=42,ts=...
     * Returns 200 if updated with json body of {@link EntitySyncStatus}.
     * Returns 204 if no update is needed.
     */
    public ResponseEntity<String> findSyncStatus(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, JsonProcessingException {
        try {
            Serializable id = idConverter.toId(readRequestParam(request,"id"));
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
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
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
     * Endpoint for finding sync statuses of multiple entities with one request.
     * Receives List of {@link EntityUpdateInfo} of client in body and looks these through.
     * Returns List of {@link EntitySyncStatus} for those that need update with respective {@link EntitySyncStatus#getStatus()}.
     * If no updated required at all, returns 204 without body.
     *
     * POST /api/core/entity/sync
     *
     */
    public ResponseEntity<String> findSyncStatuses(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
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
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(objectMapper.writeValueAsString(syncStatuses));
        } catch (IOException e) {
            throw new BadEntityException("invalid format for EntityLastUpdateInfo. Use json list.");
        }
    }

    /**
     * Endpoint for fetching all updates since a specific timestamp for entity table.
     * Client passes timestamp of when last client update.
     * Server returns Set of {@link EntitySyncStatus} of all entities, that have been removed, added or updated since then.
     * GET /api/core/entity/sync-all?ts=...
     *
     * Note that removed entities can only be determined when using {@link com.github.vincemann.springrapid.sync.softdelete.SoftDeleteSyncService} on service layer.
     */
    public ResponseEntity<String> findSyncStatusesSince(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, JsonProcessingException {
        long lastUpdateTimestamp = Long.parseLong(request.getParameter("ts"));

        log.debug(LogMessage.format("find sync statuses since timestamp request received. Timestamp: %s",new Date(lastUpdateTimestamp).toString()));

        List<EntitySyncStatus> syncStatuses = findUpdatesSinceTimestamp(new Timestamp(lastUpdateTimestamp));
        if (syncStatuses.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(objectMapper.writeValueAsString(syncStatuses));
    }


    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        if (!getIgnoredEndPoints().contains(getSyncEntityUrl()))
            registerEndpoint(createFetchEntitySyncStatusRequestMappingInfo(), "findSyncStatus");
        if (!getIgnoredEndPoints().contains(getSyncEntitiesUrl()))
            registerEndpoint(createFetchEntitySyncStatusesRequestMappingInfo(), "findSyncStatuses");
        if (!getIgnoredEndPoints().contains(getSyncEntitiesSinceUrl()))
            registerEndpoint(createFetchEntitySyncStatusesSinceTsRequestMappingInfo(), "findSyncStatusesSince");
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
                .paths(syncEntityUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesSinceTsRequestMappingInfo() {
        return RequestMappingInfo
                .paths(syncEntitiesSinceUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    protected RequestMappingInfo createFetchEntitySyncStatusesRequestMappingInfo() {
        return RequestMappingInfo
                .paths(syncEntitiesUrl)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    /**
     * Default is /api/core/myentity/
     * given E is MyEntity.class.
     *
     * Overwrite if needed.
     */
    protected String getBaseUrl(){
        Class<E> entityClass = (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(), SyncEntityController.class)[0];
        return "/api/core/"+entityClass.getSimpleName().toLowerCase()+"/";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initUrls();
    }

    protected void initUrls() {
        this.syncEntityUrl = getBaseUrl() + "sync";
        this.syncEntitiesUrl = getBaseUrl() + "sync";
        this.syncEntitiesSinceUrl = getBaseUrl() + "sync-all";
    }


    public S getService() {
        return service;
    }

    public String getSyncEntityUrl() {
        return syncEntityUrl;
    }

    public String getSyncEntitiesUrl() {
        return syncEntitiesUrl;
    }

    public String getSyncEntitiesSinceUrl() {
        return syncEntitiesSinceUrl;
    }

    public void setSyncEntityUrl(String syncEntityUrl) {
        this.syncEntityUrl = syncEntityUrl;
    }

    public void setSyncEntitiesUrl(String syncEntitiesUrl) {
        this.syncEntitiesUrl = syncEntitiesUrl;
    }

    public void setSyncEntitiesSinceUrl(String syncEntitiesSinceUrl) {
        this.syncEntitiesSinceUrl = syncEntitiesSinceUrl;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Autowired
    public void setService(S service) {
        this.service = service;
    }
}
