package io.github.vincemann.generic.crud.lib.test.service.request;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static ServiceRequestBuilder createBuilder(String methodName, List<Object> args,Class... abstractTypes) {

        Object[] argTypes = args.stream().map(e -> e.getClass()).collect(Collectors.toList()).toArray();
        //todo hier darf er nicht owner class bekommen, sondern abstr class wenn ich das interface als base für method search nutze
        return serviceUnderTest -> {
            try {
                return ServiceRequest.builder()
                        .args(args)
                        .serviceMethod(CrudService.class.getMethod(methodName,abstractTypes ))
                        .build();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }

}
