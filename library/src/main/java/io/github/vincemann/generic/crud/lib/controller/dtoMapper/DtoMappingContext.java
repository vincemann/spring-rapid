package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Getter
@Setter
public class DtoMappingContext<Id extends Serializable> {
    private Class<? extends IdentifiableEntity<Id>> createRequestDtoClass;
    private Class<? extends IdentifiableEntity<Id>> createReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> findReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> partialUpdateRequestDtoClass;
    private Class<? extends IdentifiableEntity<Id>> fullUpdateRequestDtoClass;
    private Class<? extends IdentifiableEntity<Id>> updateReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> findAllReturnDtoClass;


    public DtoMappingContext(){ }

    @Builder
    public DtoMappingContext(Class<? extends IdentifiableEntity<Id>> createRequestDtoClass, Class<? extends IdentifiableEntity<Id>> createReturnDtoClass, Class<? extends IdentifiableEntity<Id>> findReturnDtoClass, Class<? extends IdentifiableEntity<Id>> partialUpdateRequestDtoClass, Class<? extends IdentifiableEntity<Id>> updateReturnDtoClass, Class<? extends IdentifiableEntity<Id>> findAllReturnDtoClass,Class<? extends IdentifiableEntity<Id>> fullUpdateRequestDtoClass) {
        this.createRequestDtoClass = createRequestDtoClass;
        this.createReturnDtoClass = createReturnDtoClass;
        this.findReturnDtoClass = findReturnDtoClass;
        this.partialUpdateRequestDtoClass = partialUpdateRequestDtoClass;
        this.fullUpdateRequestDtoClass=fullUpdateRequestDtoClass;
        this.updateReturnDtoClass = updateReturnDtoClass;
        this.findAllReturnDtoClass = findAllReturnDtoClass;
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public static <Id extends Serializable> DtoMappingContext<Id> DEFAULT(Class<? extends IdentifiableEntity<Id>> defaultDtoClass){
        DtoMappingContext mc = new DtoMappingContext();
        mc.createRequestDtoClass =defaultDtoClass;
        mc.createReturnDtoClass=defaultDtoClass;
        mc.findReturnDtoClass=defaultDtoClass;
        mc.partialUpdateRequestDtoClass =defaultDtoClass;
        mc.fullUpdateRequestDtoClass=defaultDtoClass;
        mc.updateReturnDtoClass=defaultDtoClass;
        mc.findAllReturnDtoClass=defaultDtoClass;
        return mc;
    }

    public static <Id extends Serializable> DtoMappingContext<Id> WRITE_READ(
                                            Class<? extends IdentifiableEntity<Id>> writeDtoClass,
                                            Class<? extends IdentifiableEntity<Id>> readDtoClass){
        DtoMappingContext mc = new DtoMappingContext();
        mc.createRequestDtoClass =writeDtoClass;
        mc.createReturnDtoClass=readDtoClass;
        mc.findReturnDtoClass=readDtoClass;
        mc.partialUpdateRequestDtoClass =writeDtoClass;
        mc.fullUpdateRequestDtoClass=writeDtoClass;
        mc.updateReturnDtoClass=readDtoClass;
        mc.findAllReturnDtoClass=readDtoClass;
        return mc;
    }

    public static <Id extends Serializable> DtoMappingContext<Id> CREATE_UPDATE_READ(
                                                    Class<? extends IdentifiableEntity<Id>> createDtoClass,
                                                    Class<? extends IdentifiableEntity<Id>> updateDtoClass,
                                                    Class<? extends IdentifiableEntity<Id>> readDtoClass){
        DtoMappingContext mc = new DtoMappingContext();
        mc.createRequestDtoClass =createDtoClass;
        mc.createReturnDtoClass=readDtoClass;
        mc.findReturnDtoClass=readDtoClass;
        mc.partialUpdateRequestDtoClass =updateDtoClass;
        mc.fullUpdateRequestDtoClass=updateDtoClass;
        mc.updateReturnDtoClass=readDtoClass;
        mc.findAllReturnDtoClass=readDtoClass;
        return mc;
    }


}
