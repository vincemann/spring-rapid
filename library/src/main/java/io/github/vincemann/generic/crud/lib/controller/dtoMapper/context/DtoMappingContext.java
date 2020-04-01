package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import io.github.vincemann.generic.crud.lib.advice.log.LogInteraction;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@Slf4j
public class DtoMappingContext {
    private Map<DtoMappingInfo,Class<? extends IdentifiableEntity>> mappingEntries = new HashMap<>();
    private Map<DtoMappingInfo,Class<? extends IdentifiableEntity>> mappingEntriesIgnoreRole = new HashMap<>();
    private boolean ignoreRole;

    /**
     * Ignores role if no role was configured for this MappingContext.
     * If a role was configured, searching for dtoClass matching Role and Endpoint.
     * If none was found falling back to searching for dtoClass without Role Information.
     * @param dtoMappingInfo
     * @return
     */
    @LogInteraction
    public Class<? extends IdentifiableEntity> find(DtoMappingInfo dtoMappingInfo){
        if(!ignoreRole){
            Class<? extends IdentifiableEntity> dtoClass = mappingEntries.get(dtoMappingInfo);
            if(dtoClass==null){
                log.debug("Did not find Dto Class for entry: " + dtoMappingInfo);
                log.debug("Trying without Role Information");
                return notNull(findAndIgnoreRole(dtoMappingInfo),dtoMappingInfo);
            }else {
                return dtoClass;
            }
        }else {
             return notNull(findAndIgnoreRole(dtoMappingInfo),dtoMappingInfo);
        }
    }

    private Class<? extends IdentifiableEntity> findAndIgnoreRole(DtoMappingInfo dtoMappingInfo){
        DtoMappingInfo clone = new DtoMappingInfo(dtoMappingInfo);
        clone.setAuthorities(new ArrayList<>());
        return notNull(mappingEntriesIgnoreRole.get(clone),clone);
    }

    private Class<? extends IdentifiableEntity> notNull(Class<? extends IdentifiableEntity> clazz, DtoMappingInfo info){
        if(clazz==null){
            throw new IllegalArgumentException("No DtoClass mapped for info: "  + info);
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

    Map<DtoMappingInfo, Class<? extends IdentifiableEntity>> getMappingEntriesIgnoreRole() {
        return mappingEntriesIgnoreRole;
    }

    public DtoMappingContext(){
        this.ignoreRole=true;
    }

}
