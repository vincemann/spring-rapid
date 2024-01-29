package com.github.vincemann.springrapid.core.controller.json.patch;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface JsonPatchStrategy {
    public <T> T applyPatch(T targetDto, String patchString) throws BadEntityException;
    PatchInfo createPatchInfo(String patchString)throws BadEntityException;
}
