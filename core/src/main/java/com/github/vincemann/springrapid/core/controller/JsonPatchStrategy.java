package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface JsonPatchStrategy {
    public <T> T applyPatch(IdentifiableEntity savedEntity, T targetDto, String patchString) throws BadEntityException;
}
