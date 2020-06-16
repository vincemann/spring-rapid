package com.github.vincemann.springrapid.coretest.service.resolve;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequest;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResult;
import com.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * DB_ENTITY -> get id
 *                  {@link ServiceResult#getResult()}
 *              or
 *                  from first arg in {@link ServiceRequest#getArgs()} that is of type {@link IdentifiableEntity} and has non null id
 *              in that order.
 *              Then get db entity by calling {@link org.springframework.data.repository.CrudRepository#findById(Object)}.
 *
 * SERVICE_INPUT_ENTITY -> get first arg in {@link ServiceRequest#getArgs()} that is of type {@link IdentifiableEntity} and has non null id.
 *
 *
 * SERVICE_RETURNED_ENTITY -> {@link ServiceResult#getResult()}
 *
 */
public class RapidEntityPlaceholderResolver implements EntityPlaceholderResolver {

    @Override
    public <E extends IdentifiableEntity> E resolve(EntityPlaceholder entityPlaceholder, ServiceTestContext testContext) {
        switch (entityPlaceholder) {
            case DB_ENTITY:
                Serializable id = findDbEntityId(testContext);
                Optional<IdentifiableEntity> byId = testContext.getRepository().findById(id);
                if (byId.isPresent()) {
                    return (E) byId.get();
                }
            case SERVICE_INPUT_ENTITY:
                IdentifiableEntity firstEntity = findFirstEntityWithSetId(testContext.getServiceRequest().getArgs());
                if (firstEntity == null) {
                    throw new IllegalArgumentException("Could not find Service Input entity");
                }
            case SERVICE_RETURNED_ENTITY:
                Object result = testContext.getServiceResult().getResult();
                Assert.notNull(result, "Service Result is null");
                if (result instanceof IdentifiableEntity) {
                    return (E) result;
                } else if (result instanceof Optional) {
                    return (E) ((Optional) result).get();
                } else {
                    throw new IllegalArgumentException("Result Entity is of wrong type: " + result.getClass());
                }
        }
        throw new IllegalArgumentException("Could not resolve EntityPlaceholder: " + entityPlaceholder);
    }

    /**
     * Finds id from service result entity or first Entity arg with set id
     *
     * @param testContext
     * @return
     */
    protected Serializable findDbEntityId(ServiceTestContext testContext) {
        Object result = testContext.getServiceResult().getResult();
        if (result instanceof Optional) {
            result = ((Optional) result).get();
        }
        if (result instanceof IdentifiableEntity) {
            Serializable returnedEntityId = ((IdentifiableEntity) result).getId();
            if (returnedEntityId != null) {
                return returnedEntityId;
            }
        }
        List<Object> args = testContext.getServiceRequest().getArgs();
        if (args != null) {
            IdentifiableEntity firstEntity = findFirstEntityWithSetId(args);
            if (firstEntity!=null) {
                if (firstEntity.getId() != null) {
                    return firstEntity.getId();
                }
            }
        }
        throw new IllegalArgumentException("Id for db Entity could not be found");
    }

    protected IdentifiableEntity findFirstEntityWithSetId(List<Object> args) {
        for (Object arg : args) {
            if (arg instanceof IdentifiableEntity) {
                IdentifiableEntity entity = (IdentifiableEntity) arg;
                if (entity.getId() != null) {
                    return entity;
                }
            }
        }
        return null;
    }
}
