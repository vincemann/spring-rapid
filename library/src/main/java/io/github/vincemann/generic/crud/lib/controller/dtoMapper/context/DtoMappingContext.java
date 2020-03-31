package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

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

    public Class<? extends IdentifiableEntity> get(DtoMappingInfo dtoMappingInfo){
        DtoMappingInfo clone = new DtoMappingInfo(dtoMappingInfo);
        Class<? extends IdentifiableEntity> dtoClass;
        if(ignoreRole){
            log.debug("ignoring Role of info silently");
            clone.setAuthorities(new ArrayList<>());
            dtoClass = mappingEntriesIgnoreRole.get(clone);
        }else {
             dtoClass= mappingEntries.get(clone);
        }
        if(dtoClass==null){
            throw new IllegalArgumentException("No DtoClass mapped for info: "  + clone);
        }
        return dtoClass;
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
