package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.core.controller.dto.EntityDtoPostProcessor;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves id fields, referencing parent/child entities.
 * The id resolving is done by the main {@link EntityIdResolver}.
 *
 * @see EntityIdResolver
 * @see DelegatingEntityIdResolver
 */
@Order(1000)
public class IdResolvingDtoPostProcessor implements EntityDtoPostProcessor<Object, IdAwareEntity<?>> {

    private DelegatingEntityIdResolver resolver;

    public IdResolvingDtoPostProcessor(DelegatingEntityIdResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean supports(Class<?> entityClass, Class<?> dtoClass) {
        return true;
    }

    @Override
    public void postProcessDto(Object dto, IdAwareEntity<?> entity, String... fieldsToCheck) {
        resolver.setResolvedIds(dto,entity,fieldsToCheck);
    }

    @Transactional
    @Override
    public void postProcessEntity(IdAwareEntity<?> entity, Object dto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        resolver.setResolvedEntities(entity,dto,fieldsToCheck);
    }

}
