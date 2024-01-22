package com.github.vincemann.springrapid.sync.util;

import com.github.vincemann.springrapid.core.model.IAuditingEntity;
import com.github.vincemann.springrapid.sync.service.EqualsMethod;

import java.util.Collection;
import java.util.Optional;

public class CollectionUtils {

    public static boolean customEquals(Collection<IAuditingEntity<Long>> first, Collection<IAuditingEntity<Long>> second, EqualsMethod<IAuditingEntity<Long>> equalsMethod){
        for (IAuditingEntity<Long> entity : first){
            Optional<IAuditingEntity<Long>> other = second.stream().filter(e -> e.getId().equals(entity.getId())).findFirst();
            if (other.isEmpty()){
                return false;
            }
            if (!equalsMethod.equals(entity,other.get())){
                return false;
            }
        }
        return true;
    }
}
