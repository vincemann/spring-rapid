package com.github.vincemann.springrapid.audit;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface AuditingJpaRepository<E extends AuditingEntity<Id>,Id extends Serializable>
        extends JpaRepository<E, Id>,
        AuditingRepository<Id>
{

}
