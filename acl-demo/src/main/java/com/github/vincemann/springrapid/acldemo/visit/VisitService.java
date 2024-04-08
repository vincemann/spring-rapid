package com.github.vincemann.springrapid.acldemo.visit;

import com.github.vincemann.springrapid.acldemo.visit.dto.CreateVisitDto;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

import java.util.Optional;

public interface VisitService {


    Visit create(CreateVisitDto dto) throws BadEntityException, EntityNotFoundException;
    void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException;
    void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException;

    Optional<Visit> find(long id);
}
