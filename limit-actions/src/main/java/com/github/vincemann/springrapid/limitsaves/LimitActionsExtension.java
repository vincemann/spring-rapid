package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Only allows certain amount of service method invocations in specified time interval.
 */
@Slf4j
public abstract class LimitActionsExtension extends BasicServiceExtension<CrudService>
{

    private int maxAmountActions;
    private Map<String, Integer> principal_amountActions_history = new HashMap<>();
    private long timeInterval;
    private Date lastReset;

    /**
     *
     * @param maxAmountActions
     * @param timeInterval  in millis
     */
    public LimitActionsExtension(int maxAmountActions, long timeInterval) {
        this.maxAmountActions = maxAmountActions;
        this.timeInterval = timeInterval;
        this.lastReset = new Date();
    }

    public void actionPerformed() {
        String principal = getPrincipal();
        principal_amountActions_history.merge(principal, 1, Integer::sum);
    }

    public void checkLimit() {
        checkTimeReset();
        String principal = getPrincipal();
        Integer amount = principal_amountActions_history.get(principal);
        if (amount != null) {
            if (amount >= maxAmountActions) {
                throw new TooManyRequestsException("Max amount of Performed Actions is reached");
            }
        }
    }

    protected void checkTimeReset(){
        Date now = new Date();
        if (now.getTime() - lastReset.getTime() > timeInterval) {
            reset();
        }
    }

    protected String getPrincipal() {
        return RapidSecurityContext.getName();
    }


    public void reset() {
        this.principal_amountActions_history.clear();
        this.lastReset = new Date();
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
