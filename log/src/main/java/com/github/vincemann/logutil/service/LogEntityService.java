package com.github.vincemann.logutil.service;

import com.github.vincemann.logutil.model.LogChild;
import com.github.vincemann.logutil.model.LogEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.checkerframework.checker.nullness.Opt;

import java.util.Optional;

public interface LogEntityService extends CrudService<LogEntity,Long> {

    public Optional<LogEntity> findByIdAndLoadCol1(Long id) throws BadEntityException;
}

