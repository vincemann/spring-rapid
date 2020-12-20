package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class LimitSavesExtension
        extends BasicServiceExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private int maxAmountSavedEntities;
    private Map<String, Integer> principal_amountCreatedEntities_history = new HashMap<>();
    private long timeInterval;


    /**
     *
     * @param maxAmountSavedEntities
     * @param timeInterval  in millis
     */
    public LimitSavesExtension(int maxAmountSavedEntities, long timeInterval) {
        this.maxAmountSavedEntities = maxAmountSavedEntities;
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
                log.debug("principal: " + principal + " tried to create more entities of type: " + getEntityClass() + " then allowed in time period");
                throw new TooManyRequestsException("Max amount of created Entites of type : " + getEntityClass() + " is exceeded");
            } else {
                principal_amountCreatedEntities_history.put(principal, amount + 1);
            }
        }
    }

    protected String getPrincipal() {
        return RapidSecurityContext.getName();
    }

    @Override
    public Class getEntityClass(){
        return getLast().getEntityClass();
    }

    protected void reset() {
        this.principal_amountCreatedEntities_history.clear();
    }

    protected int getMaxAmountSavedEntities() {
        return maxAmountSavedEntities;
    }

    protected Map<String, Integer> getPrincipal_amountCreatedEntities_history() {
        return principal_amountCreatedEntities_history;
    }


    protected long getTimeInterval() {
        return timeInterval;
    }
}
