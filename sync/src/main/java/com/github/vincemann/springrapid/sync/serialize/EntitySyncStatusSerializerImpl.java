package com.github.vincemann.springrapid.sync.serialize;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.sync.dto.EntitySyncStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * convert {@link EntitySyncStatus} to string and vice versa, trying to optimize space, resulting in less network load
 * -> this will be executed all the time
 *
 * This class can also be copied and used by client.
 */
public class EntitySyncStatusSerializerImpl implements EntitySyncStatusSerializer {
    public static final String DELIMITER = ":";
    @Override
    public String serialize(EntitySyncStatus syncStatus) {
        return syncStatus.getId()+syncStatus.getStatus();
    }

    /**
     * returns idStatus:idStatus...
     * i.E. "42a:45u:46r"
     */
    @Override
    public String serialize(Set<EntitySyncStatus> entitySyncStatuses) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (EntitySyncStatus entitySyncStatus : entitySyncStatuses) {
            result.append(serialize(entitySyncStatus));
            if (++count < entitySyncStatuses.size()){
                result.append(DELIMITER);
            }
        }
        return result.toString();
    }

    @Override
    public EntitySyncStatus deserialize(String statusString) throws BadEntityException {
        if (statusString.length() >= 2) {
            // Get the last character
            Character status = statusString.charAt(statusString.length() - 1);

            // Get the rest of the characters
            String id = statusString.substring(0, statusString.length() - 1);

            return new EntitySyncStatus(id,status);
        } else {
            // Handle cases where the string is too short to split
            throw new BadEntityException("status string to too short");
        }
    }

    @Override
    public Set<EntitySyncStatus> deserializeSetString(String statusString) throws BadEntityException {
        Set<EntitySyncStatus> result = new HashSet<>();
        for (String element : statusString.split(DELIMITER)) {
            // cant use stream api bc of checked exception
            EntitySyncStatus syncStatus = deserialize(element);
            result.add(syncStatus);
        }
        return result;
    }
}
