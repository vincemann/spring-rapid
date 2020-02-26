package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Getter
@Setter
public class DtoMappingContext {
    protected Class<? extends IdentifiableEntity> createRequestDtoClass;
    protected Class<? extends IdentifiableEntity> createReturnDtoClass;
    protected Class<? extends IdentifiableEntity> findReturnDtoClass;
    protected Class<? extends IdentifiableEntity> partialUpdateRequestDtoClass;
    protected Class<? extends IdentifiableEntity> fullUpdateRequestDtoClass;
    protected Class<? extends IdentifiableEntity> updateReturnDtoClass;
    protected Class<? extends IdentifiableEntity> findAllReturnDtoClass;


    public DtoMappingContext(){ }

    @Builder
    public DtoMappingContext(Class<? extends IdentifiableEntity> createRequestDtoClass, Class<? extends IdentifiableEntity> createReturnDtoClass, Class<? extends IdentifiableEntity> findReturnDtoClass, Class<? extends IdentifiableEntity> partialUpdateRequestDtoClass, Class<? extends IdentifiableEntity> updateReturnDtoClass, Class<? extends IdentifiableEntity> findAllReturnDtoClass,Class<? extends IdentifiableEntity> fullUpdateRequestDtoClass) {
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
    public static <Id extends Serializable> DtoMappingContext DEFAULT(Class<? extends IdentifiableEntity> defaultDtoClass){
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

    public static <Id extends Serializable> DtoMappingContext WRITE_READ(
                                            Class<? extends IdentifiableEntity> writeDtoClass,
                                            Class<? extends IdentifiableEntity> readDtoClass){
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

    public static <Id extends Serializable> DtoMappingContext CREATE_UPDATE_READ(
                                                    Class<? extends IdentifiableEntity> createDtoClass,
                                                    Class<? extends IdentifiableEntity> updateDtoClass,
                                                    Class<? extends IdentifiableEntity> readDtoClass){
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
