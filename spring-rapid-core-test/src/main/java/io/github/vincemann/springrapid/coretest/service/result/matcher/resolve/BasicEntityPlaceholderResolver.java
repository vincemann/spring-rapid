package io.github.vincemann.springrapid.coretest.service.result.matcher.resolve;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public class BasicEntityPlaceholderResolver implements EntityPlaceholderResolver {

    @Override
    public IdentifiableEntity resolve(EntityPlaceholder entityPlaceholder, ServiceTestContext testContext) {
        switch (entityPlaceholder) {
            case DB_ENTITY:
                Serializable id = findDbEntityId(testContext);
                Optional<IdentifiableEntity> byId = testContext.getRepository().findById(id);
                if (byId.isPresent()) {
                    return byId.get();
                }
            case SERVICE_INPUT_ENTITY:
                IdentifiableEntity firstEntity = findFirstEntityWithSetId(testContext.getServiceResult().getServiceRequest().getArgs());
                if (firstEntity == null) {
                    throw new IllegalArgumentException("Could not find Service Input entity");
                }
            case SERVICE_RETURNED_ENTITY:
                Object result = testContext.getServiceResult().getResult();
                Assert.notNull(result, "Service Result is null");
                if (result instanceof IdentifiableEntity) {
                    return ((IdentifiableEntity) result);
                } else if (result instanceof Optional) {
                    return (IdentifiableEntity) ((Optional) result).get();
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
    private Serializable findDbEntityId(ServiceTestContext testContext) {
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
        List<Object> args = testContext.getServiceResult().getServiceRequest().getArgs();
        if (args != null) {
            IdentifiableEntity firstEntity = findFirstEntityWithSetId(args);
            if (firstEntity.getId() != null) {
                return firstEntity.getId();
            }
        }
        throw new IllegalArgumentException("Id for db Entity could not be found");
    }

    private IdentifiableEntity findFirstEntityWithSetId(List<Object> args) {
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
