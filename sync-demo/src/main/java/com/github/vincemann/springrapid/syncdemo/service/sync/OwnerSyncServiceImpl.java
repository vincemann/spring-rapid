package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.service.DefaultSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;

@Service
public class OwnerSyncServiceImpl
        extends DefaultSyncService<Owner,Long>
                implements OwnerSyncService
{


    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatusesSinceTimestampWithTelnrPrefix(Timestamp lastClientUpdate, String prefix) {
        return findEntitySyncStatusesSinceTimestamp(lastClientUpdate,new WithTelPrefix(prefix));
    }

    @AllArgsConstructor
    private static class WithTelPrefix implements Specification<Owner> {

        private String prefix;
        @Nullable
        @Override
        public Predicate toPredicate(Root<Owner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.like(root.get("telephone"), prefix+"%");
        }
    }
}
