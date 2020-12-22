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
        extends LimitActionsExtension {


    public LimitSavesExtension(int maxAmountActions, long timeInterval) {
        super(maxAmountActions, timeInterval);
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        checkAmountEntitiesCreated();
        IdentifiableEntity saved = getNext().save(entity);
        newEntityCreated();
        return saved;
    }


}
