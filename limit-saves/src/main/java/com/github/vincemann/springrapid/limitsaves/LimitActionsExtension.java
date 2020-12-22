package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LimitActionsExtension extends BasicServiceExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private int maxAmountActions;
    private Map<String, Integer> principal_amountActions_history = new HashMap<>();
    private long timeInterval;

    /**
     *
     * @param maxAmountActions
     * @param timeInterval  in millis
     */
    public LimitActionsExtension(int maxAmountActions, long timeInterval) {
        this.maxAmountActions = maxAmountActions;
        this.timeInterval = timeInterval;
    }

    public void newEntityCreated() {
        String principal = getPrincipal();
        principal_amountActions_history.merge(principal, 1, Integer::sum);
    }

    public void checkAmountEntitiesCreated() {
        String principal = getPrincipal();
        Integer amount = principal_amountActions_history.get(principal);
        if (amount != null) {
            if (amount >= maxAmountActions) {
                log.debug("principal: " + principal + " tried to create more entities of type: " + getEntityClass() + " then allowed in time period");
                throw new TooManyRequestsException("Max amount of created Entites of type : " + getEntityClass() + " is exceeded");
            } else {
                principal_amountActions_history.put(principal, amount + 1);
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
        this.principal_amountActions_history.clear();
    }

    protected int getMaxAmountActions() {
        return maxAmountActions;
    }

    protected Map<String, Integer> getPrincipal_amountActions_history() {
        return principal_amountActions_history;
    }


    protected long getTimeInterval() {
        return timeInterval;
    }
}
