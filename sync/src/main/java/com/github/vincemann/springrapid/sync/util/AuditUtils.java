package com.github.vincemann.springrapid.sync.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;

public class AuditUtils {

    public static AuditId toId(IdentifiableEntity<?> entity){
        return new AuditId(entity.getClass().getName(),entity.getId().toString());
    }

    public static AuditId toId(LastFetchInfo fetchInfo){
        return new AuditId(fetchInfo.getClass().getName(),fetchInfo.getId());
    }
}
