package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.LazyExceptionItem;
import com.github.vincemann.springrapid.coredemo.repo.LazyExceptionItemRepository;
import com.github.vincemann.springrapid.coredemo.service.LazyItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Service
public class JpaLazyItemService
        extends JPACrudService<LazyExceptionItem,Long, LazyExceptionItemRepository>
             implements LazyItemService {

    @Override
    public Class<?> getTargetClass() {
        return JpaLazyItemService.class;
    }

    @Transactional
    @Override
    public LazyExceptionItem update(LazyExceptionItem update, Boolean full,String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return super.update(update, full,fieldsToRemove);
    }
}
