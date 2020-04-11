package io.github.vincemann.springrapid.coretest.service.request;

import io.github.vincemann.springrapid.core.util.Lists;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;
import java.util.List;

public class CrudServiceRequestBuilders {

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave) {
        return createBuilder("save", Lists.newArrayList(entityToSave),IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity) {
        return createBuilder("update", Lists.newArrayList(updateEntity,true),IdentifiableEntity.class,Boolean.class);
    }

    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity) {
        return createBuilder("update", Lists.newArrayList(updateEntity,false),IdentifiableEntity.class, Boolean.class);
    }

    public static ServiceRequestBuilder deleteById(Serializable id) {
        return createBuilder("deleteById", Lists.newArrayList(id),Serializable.class);
    }

    public static ServiceRequestBuilder findById(Serializable id) {
        return createBuilder("findById", Lists.newArrayList(id),Serializable.class);
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
