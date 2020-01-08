package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Getter
@Setter
public class MappingContext<Id extends Serializable> {
    private Class<? extends IdentifiableEntity<Id>> createArgDtoClass;
    private Class<? extends IdentifiableEntity<Id>> createReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> findReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> updateArgDtoClass;
    private Class<? extends IdentifiableEntity<Id>> updateReturnDtoClass;
    private Class<? extends IdentifiableEntity<Id>> findAllReturnDtoClass;


    public MappingContext(){ }

    @Builder
    public MappingContext(Class<? extends IdentifiableEntity<Id>> createArgDtoClass, Class<? extends IdentifiableEntity<Id>> createReturnDtoClass, Class<? extends IdentifiableEntity<Id>> findReturnDtoClass, Class<? extends IdentifiableEntity<Id>> updateArgDtoClass, Class<? extends IdentifiableEntity<Id>> updateReturnDtoClass, Class<? extends IdentifiableEntity<Id>> findAllReturnDtoClass) {
        this.createArgDtoClass = createArgDtoClass;
        this.createReturnDtoClass = createReturnDtoClass;
        this.findReturnDtoClass = findReturnDtoClass;
        this.updateArgDtoClass = updateArgDtoClass;
        this.updateReturnDtoClass = updateReturnDtoClass;
        this.findAllReturnDtoClass = findAllReturnDtoClass;
    }

    /**
     * Uses one dto class for all crud operations
     * @param defaultDtoClass
     * @return
     */
    public static <Id extends Serializable>MappingContext<Id> DEFAULT(Class<? extends IdentifiableEntity<Id>> defaultDtoClass){
        MappingContext mc = new MappingContext();
        mc.createArgDtoClass=defaultDtoClass;
        mc.createReturnDtoClass=defaultDtoClass;
        mc.findReturnDtoClass=defaultDtoClass;
        mc.updateArgDtoClass=defaultDtoClass;
        mc.updateReturnDtoClass=defaultDtoClass;
        mc.findAllReturnDtoClass=defaultDtoClass;
        return mc;
    }

    public static <Id extends Serializable>MappingContext<Id> WRITE_READ(
                                            Class<? extends IdentifiableEntity<Id>> writeDtoClass,
                                            Class<? extends IdentifiableEntity<Id>> readDtoClass){
        MappingContext mc = new MappingContext();
        mc.createArgDtoClass=writeDtoClass;
        mc.createReturnDtoClass=readDtoClass;
        mc.findReturnDtoClass=readDtoClass;
        mc.updateArgDtoClass=writeDtoClass;
        mc.updateReturnDtoClass=readDtoClass;
        mc.findAllReturnDtoClass=readDtoClass;
        return mc;
    }

    public static <Id extends Serializable> MappingContext<Id> CREATE_UPDATE_READ(
                                                    Class<? extends IdentifiableEntity<Id>> createDtoClass,
                                                    Class<? extends IdentifiableEntity<Id>> updateDtoClass,
                                                    Class<? extends IdentifiableEntity<Id>> readDtoClass){
        MappingContext mc = new MappingContext();
        mc.createArgDtoClass=createDtoClass;
        mc.createReturnDtoClass=readDtoClass;
        mc.findReturnDtoClass=readDtoClass;
        mc.updateArgDtoClass=updateDtoClass;
        mc.updateReturnDtoClass=readDtoClass;
        mc.findAllReturnDtoClass=readDtoClass;
        return mc;
    }


}
