package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.model.Visit;

public interface VisitService extends CrudService<Visit,Long> {
    public void subscribeOwner(Owner owner, Visit visit);
    public void unsubscribeOwner(Owner owner, Visit visit) throws BadEntityException;
}
