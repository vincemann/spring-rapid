package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.LogEntity;
import com.github.vincemann.logutil.repo.EagerSingleLogChildRepository;
import com.github.vincemann.logutil.repo.LogEntityRepository;
import com.github.vincemann.logutil.service.EagerSingleLogChildService;
import com.github.vincemann.logutil.service.LogEntityService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;


@Service
@ServiceComponent
public class JpaLogEntityService extends JPACrudService<LogEntity,Long, LogEntityRepository> implements LogEntityService {


    @Transactional
    @Override
    public Optional<LogEntity> findByIdAndLoadCol1(Long id) throws BadEntityException {
        Optional<LogEntity> byId = getRepository().findById(id);
        LogEntity logEntity = byId.get();
        logEntity.getLazyChildren1().size();
        return Optional.of(logEntity);
    }
}
