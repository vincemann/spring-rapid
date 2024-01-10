package com.github.vincemann.springrapid.sync.serialize;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.sync.dto.EntitySyncStatus;

import java.util.Set;

public interface EntitySyncStatusSerializer {

    public String serialize(EntitySyncStatus status);

    EntitySyncStatus deserialize(String statusString) throws BadEntityException;
    String serialize(Set<EntitySyncStatus> entitySyncStatuses);

    Set<EntitySyncStatus> deserializeSetString(String statusString) throws BadEntityException;
}
