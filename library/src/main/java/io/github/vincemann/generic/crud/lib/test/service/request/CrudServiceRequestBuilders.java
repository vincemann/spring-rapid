package io.github.vincemann.generic.crud.lib.test.service.request;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrudServiceRequestBuilders {

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave) {
        return createBuilder("save", Arrays.asList(entityToSave));
    }

    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity) {
        return createBuilder("update", Arrays.asList(updateEntity,true));
    }

    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity) {
        return createBuilder("update", Arrays.asList(updateEntity,false));
    }

    public static ServiceRequestBuilder deleteById(Serializable id) {
        return createBuilder("deleteById", Arrays.asList(id));
    }

    public static ServiceRequestBuilder findById(Serializable id) {
        return createBuilder("findById", Arrays.asList(id));
    }

    private static ServiceRequestBuilder createBuilder(String methodName, List<Object> args) {

        Object[] argTypes = args.stream().map(e -> e.getClass()).collect(Collectors.toList()).toArray();
        Class<?>[] argTypeArray //= Arrays.copyOf(argTypes, args.size(), Class[].class);
        = {IdentifiableEntity.class};
        return serviceUnderTest -> {
            try {
                return ServiceRequest.builder()
                        .args(args)
                        .serviceMethod(CrudService.class.getMethod(methodName, argTypeArray))
                        .build();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }

}
