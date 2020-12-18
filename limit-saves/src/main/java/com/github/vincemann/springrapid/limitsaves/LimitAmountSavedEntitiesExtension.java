package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class LimitAmountSavedEntitiesExtension
        extends BasicServiceExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    @Getter
    private int maxAmountSavedEntities;
    @Getter
    private Map<String, Integer> principal_amountCreatedEntities_history = new HashMap<>();
    private Class<?> entityClass;
    private long timeInterval;

    /**
     *
     * @param maxAmountSavedEntities
     * @param entityClass
     * @param timeInterval  in millis
     */
    public LimitAmountSavedEntitiesExtension(int maxAmountSavedEntities, Class<?> entityClass, long timeInterval) {
        this.maxAmountSavedEntities = maxAmountSavedEntities;
        this.entityClass = entityClass;
        this.timeInterval = timeInterval;
    }


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        checkAmountEntitiesCreated();
        IdentifiableEntity saved = getNext().save(entity);
        newEntityCreated();
        return saved;
    }

    public void newEntityCreated() {
        String principal = getPrincipal();
        principal_amountCreatedEntities_history.merge(principal, 1, Integer::sum);
    }

    public void checkAmountEntitiesCreated() {
        String principal = getPrincipal();
        Integer amount = principal_amountCreatedEntities_history.get(principal);
        if (amount != null) {
            if (amount >= maxAmountSavedEntities) {
                log.debug("principal: " + principal + " tried to create more entities of type: " + entityClass + " then allowed in time period");
                throw new TooManyRequestsException("Max amount of created Entites of type : " + entityClass + " is exceeded");
            } else {
                principal_amountCreatedEntities_history.put(principal, amount + 1);
            }
        }
    }

    protected String getPrincipal() {
        return RapidSecurityContext.getName();
    }

    public boolean supports(Class<?> clazz) {
        return entityClass.equals(clazz);
    }

    public void reset() {
        this.principal_amountCreatedEntities_history.clear();
    }

}
