package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Visit;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long> {
    public void giveOwnerReadPermissionForVisit(Owner owner, Visit visit);
    public void removeOwnersReadPermissionForVisit(Owner owner, Visit visit) throws BadEntityException;
}
