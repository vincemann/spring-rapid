package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.util.Optional;

public interface IllnessService extends CrudService<Illness,Long> {
    Optional<Illness> findByName(String name);
}
