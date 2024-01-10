package com.github.vincemann.springrapid.audit;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

public class AuditingJpaCrudService
        <
                E extends AuditingEntity<Id>,
                Id extends Serializable,
                R extends JpaRepository<E,Id> & AuditingRepository<Id>>
        extends JPACrudService<E,Id,R>
        implements AuditingService<Id> {

    @Transactional
    @Override
    public Date findLastModifiedDate(Id id) {
        return getRepository().findLastModifiedDateByIdEquals(id);
    }


}
