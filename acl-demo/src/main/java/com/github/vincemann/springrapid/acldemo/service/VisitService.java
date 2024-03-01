package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.model.Visit;

public interface VisitService extends CrudService<Visit,Long> {

    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException;
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException;
}
