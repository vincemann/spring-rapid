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
public class PetSyncServiceImpl
        extends DefaultSyncService<Pet,Long>
                implements PetSyncService {

    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatusesSinceTimestampOfOwner(Timestamp timestamp, long ownerId) {
        return findEntitySyncStatusesSinceTimestamp(timestamp,new WithOwnerId(ownerId));
    }

    @AllArgsConstructor
    private static class WithOwnerId implements Specification<Pet> {

        private Long ownerId;
        @Nullable
        @Override
        public Predicate toPredicate(Root<Pet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.<Owner>get("owner").get("id"), ownerId);
        }
    }
}
