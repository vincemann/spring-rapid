package com.github.vincemann.springrapid.coretest.service.request;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Use these preconfigured {@link ServiceRequestBuilder}s to build {@link ServiceRequest}s for typical Tests of a {@link CrudService} method.
 */
public class CrudServiceRequestBuilders {

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave,Boolean exceptionWanted) {
        return createBuilder("save", Lists.newArrayList(entityToSave),exceptionWanted, IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder save(IdentifiableEntity entityToSave) {
        return save(entityToSave,false);
    }



    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity,Boolean exceptionWanted) {
        return createBuilder("fullUpdate", Lists.newArrayList(updateEntity),exceptionWanted, IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder update(IdentifiableEntity updateEntity) {
        return update(updateEntity,false);
    }



    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity,Boolean exceptionWanted,String... fieldsToUpdate) {
        return createBuilder("partialUpdate", Lists.newArrayList(updateEntity,fieldsToUpdate),exceptionWanted, IdentifiableEntity.class, String[].class);
    }


    public static ServiceRequestBuilder partialUpdate(IdentifiableEntity updateEntity, String... fieldsToUpdate) {
        return partialUpdate(updateEntity,false,fieldsToUpdate);
    }



    public static ServiceRequestBuilder softUpdate(IdentifiableEntity updateEntity,Boolean exceptionWanted) {
        return createBuilder("softUpdate", Lists.newArrayList(updateEntity),exceptionWanted, IdentifiableEntity.class);
    }

    public static ServiceRequestBuilder softUpdate(IdentifiableEntity updateEntity) {
        return softUpdate(updateEntity,false);
    }


    public static ServiceRequestBuilder deleteById(Serializable id,Boolean exceptionWanted) {
        return createBuilder("deleteById", Lists.newArrayList(id),exceptionWanted, Serializable.class);
    }

    public static ServiceRequestBuilder deleteById(Serializable id) {
        return deleteById(id,false);
    }

    public static ServiceRequestBuilder findById(Serializable id,Boolean exceptionWanted) {
        return createBuilder("findById", Lists.newArrayList(id),exceptionWanted, Serializable.class);
    }

    public static ServiceRequestBuilder findById(Serializable id) {
        return findById(id,false);
    }

    protected static ServiceRequestBuilder createBuilder(String methodName, List<Object> args,Boolean exceptionWanted,  Class... types) {
        return serviceUnderTest -> {
            Method method = MethodUtils.getMatchingMethod(serviceUnderTest.getClass(), methodName, types);
            if (method == null) {
                throw new IllegalArgumentException("Cant find method: " + methodName + " in service: " + serviceUnderTest);
            }
//            method.get
            return ServiceRequest.builder()
                    .args(args)
                    .serviceMethod(method)
                    .exceptionWanted(exceptionWanted)
                    .build();
        };
    }

}
