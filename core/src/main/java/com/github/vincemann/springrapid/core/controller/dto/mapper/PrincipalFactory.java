package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

public interface PrincipalFactory {

    public Principal create(IdentifiableEntity<?> entity);
}
