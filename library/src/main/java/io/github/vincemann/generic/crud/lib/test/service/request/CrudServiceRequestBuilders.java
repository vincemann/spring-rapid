package io.github.vincemann.generic.crud.lib.test.service.request;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CrudServiceRequestBuilders {

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave) {
        return createBuilder("save", Arrays.asList(entityToSave),IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity) {
        return createBuilder("update", Arrays.asList(updateEntity,true),IdentifiableEntity.class,Boolean.class);
    }

    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity) {
        return createBuilder("update", Arrays.asList(updateEntity,false),IdentifiableEntity.class, Boolean.class);
    }

    public static ServiceRequestBuilder deleteById(Serializable id) {
        return createBuilder("deleteById", Arrays.asList(id),Serializable.class);
    }

    public static ServiceRequestBuilder findById(Serializable id) {
        return createBuilder("findById", Arrays.asList(id),Serializable.class);
    }

    protected static ServiceRequestBuilder createBuilder(String methodName, List<Object> args,Class... types) {
        return serviceUnderTest -> {
            try {
                return ServiceRequest.builder()
                        .args(args)
                        .serviceMethod(CrudService.class.getMethod(methodName,types ))
                        .build();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }

}
