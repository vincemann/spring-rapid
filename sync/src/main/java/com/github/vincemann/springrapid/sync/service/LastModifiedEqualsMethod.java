package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.IAuditingEntity;

public class LastModifiedEqualsMethod implements EqualsMethod<IAuditingEntity<Long>> {

    @Override
    public boolean equals(IAuditingEntity<Long> first, IAuditingEntity<Long> second) {
        return first.getLastModifiedDate().equals(second.getLastModifiedDate());
    }
}
