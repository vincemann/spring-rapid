package com.github.vincemann.springrapid.coretest.service.request;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Use these preconfigured {@link ServiceRequestBuilder}s to build {@link ServiceRequest}s for typical Tests of a {@link CrudService} method.
 */
public class CrudServiceRequestBuilders {

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave) {
        return createBuilder("save", Lists.newArrayList(entityToSave), IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity) {
        return createBuilder("update", Lists.newArrayList(updateEntity, true), IdentifiableEntity.class, Boolean.class);
    }

    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity) {
        return createBuilder("update", Lists.newArrayList(updateEntity, false), IdentifiableEntity.class, Boolean.class);
    }

    public static ServiceRequestBuilder deleteById(Serializable id) {
        return createBuilder("deleteById", Lists.newArrayList(id), Serializable.class);
    }

    public static ServiceRequestBuilder findById(Serializable id) {
        return createBuilder("findById", Lists.newArrayList(id), Serializable.class);
    }

    protected static ServiceRequestBuilder createBuilder(String methodName, List<Object> args, Class... types) {
        return serviceUnderTest -> {
            Method method = MethodUtils.getMatchingMethod(serviceUnderTest.getClass(), methodName, types);
            if (method == null) {
                throw new IllegalArgumentException("Cant find method: " + methodName + " in service: " + serviceUnderTest);
            }
            return ServiceRequest.builder()
                    .args(args)
                    .serviceMethod(method)
                    .build();
        };
    }

}
