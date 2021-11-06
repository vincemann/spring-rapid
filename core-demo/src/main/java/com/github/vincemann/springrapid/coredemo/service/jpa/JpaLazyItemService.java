package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.LazyItem;
import com.github.vincemann.springrapid.coredemo.repo.LazyItemRepository;
import com.github.vincemann.springrapid.coredemo.service.LazyItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Service
public class JpaLazyItemService
        extends JPACrudService<LazyItem,Long, LazyItemRepository>
             implements LazyItemService {

    @Override
    public Class<?> getTargetClass() {
        return JpaLazyItemService.class;
    }

    @Transactional
    @Override
    public LazyItem update(LazyItem update, Boolean full) throws EntityNotFoundException, BadEntityException {
        return super.update(update, full);
    }
}
