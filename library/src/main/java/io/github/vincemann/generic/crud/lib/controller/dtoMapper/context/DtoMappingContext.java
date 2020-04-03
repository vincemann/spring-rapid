package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import io.github.vincemann.generic.crud.lib.advice.log.LogInteraction;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
@Slf4j
public class DtoMappingContext {
    private Map<DtoMappingInfo, Class<? extends IdentifiableEntity>> mappingEntries = new HashMap<>();
    //private Map<DtoMappingInfo,Class<? extends IdentifiableEntity>> mappingEntriesIgnoreRole = new HashMap<>();
    private boolean ignoreRole;

    DtoMappingContext() {
        this.ignoreRole = true;
    }

    /**
     * Ignores role if no role was configured for this MappingContext.
     * If a role was configured, searching for dtoClass matching Role and Endpoint.
     * If none was found falling back to searching for dtoClass without Role Information.
     *
     * @param dtoMappingInfo
     * @return
     */
    @LogInteraction
    public Class<? extends IdentifiableEntity> find(DtoMappingInfo dtoMappingInfo) {
        AtomicReference<DtoMappingInfo> bestMatch = new AtomicReference<>();
        int mostMatchingRoles = 0;
        mappingEntries.entrySet().stream().forEach(entry -> {
            DtoMappingInfo info = entry.getKey();
            boolean hasAllRoles = true;
            int matchingRoles = 0;
            for (String authority : info.getAuthorities()) {
                if(!dtoMappingInfo.getAuthorities().contains(authority)){
                    //misses a role
                    hasAllRoles=false;
                }else {
                    matchingRoles++;
                }
            }

            boolean match =  hasAllRoles
                    && dtoMappingInfo.getDirection().equals(info.getDirection())
                    && dtoMappingInfo.getEndpoint().equals(info.getEndpoint());
            if(match){
                if(matchingRoles>mostMatchingRoles){
                    bestMatch.set(info);
                }
            }
        });
        if(bestMatch.get()==null){
            log.debug("Did not find Dto Class for entry: " + dtoMappingInfo);
            log.debug("Trying without Role Information");
            return notNull(findWithoutRole(dtoMappingInfo), dtoMappingInfo);
        }
        return mappingEntries.get(bestMatch.get());
    }

    private Class<? extends IdentifiableEntity> findWithoutRole(DtoMappingInfo dtoMappingInfo) {
        DtoMappingInfo clone = new DtoMappingInfo(dtoMappingInfo);
        clone.setAuthorities(new ArrayList<>());
        return notNull(mappingEntries.get(clone), clone);
    }

    private Class<? extends IdentifiableEntity> notNull(Class<? extends IdentifiableEntity> clazz, DtoMappingInfo info) {
        if (clazz == null) {
            throw new IllegalArgumentException("No DtoClass mapped for info: " + info);
        }
        return clazz;
    }

    Map<DtoMappingInfo, Class<? extends IdentifiableEntity>> getMappingEntries() {
        return mappingEntries;
    }

    void setMappingEntries(Map<DtoMappingInfo, Class<? extends IdentifiableEntity>> mappingEntries) {
        this.mappingEntries = mappingEntries;
    }

    boolean isIgnoreRole() {
        return ignoreRole;
    }

    void setIgnoreRole(boolean ignoreRole) {
        this.ignoreRole = ignoreRole;
    }

//    Map<DtoMappingInfo, Class<? extends IdentifiableEntity>> getMappingEntriesIgnoreRole() {
//        return mappingEntriesIgnoreRole;
//    }

}
