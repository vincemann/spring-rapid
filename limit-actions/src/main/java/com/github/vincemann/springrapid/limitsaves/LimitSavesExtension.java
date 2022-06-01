package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LimitSavesExtension
        extends LimitActionsExtension
            implements CrudServiceExtension<CrudService> {


    public LimitSavesExtension(int maxAmountActions, long timeInterval) {
        super(maxAmountActions, timeInterval);
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        checkLimit();
        IdentifiableEntity saved = getNext().save(entity);
        actionPerformed();
        return saved;
    }

    @Override
    public void checkLimit() {
        try{
            super.checkLimit();
        }catch (TooManyRequestsException e){
            throw new TooManyRequestsException("principal: " + getContactInformation() + " tried to create more entities of type: " + getEntityClass() + " then allowed in time period",e);
        }
    }

    @Override
    public Class getEntityClass(){
        return getLast().getEntityClass();
    }
}
