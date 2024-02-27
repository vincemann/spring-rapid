package com.github.vincemann.springrapid.autobidir.resolveid;

import com.github.vincemann.springrapid.core.controller.dto.EntityDtoPostProcessor;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Resolves id fields, referencing parent/child entities.
 * The id resolving is done by the main {@link EntityIdResolver}.
 *
 * @see EntityIdResolver
 * @see DelegatingEntityIdResolver
 */
@Order(1000)
public class IdResolvingDtoPostProcessor implements EntityDtoPostProcessor<Object, IdentifiableEntity<?>> {

    private DelegatingEntityIdResolver resolver;

    public IdResolvingDtoPostProcessor(DelegatingEntityIdResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean supports(Class<?> entityClass, Class<?> dtoClass) {
        return true;
    }

    @Override
    public void postProcessDto(Object dto, IdentifiableEntity<?> entity, String... fieldsToCheck) {
        resolver.setResolvedIds(dto,entity,fieldsToCheck);
    }

    // might need entityManager.merge?
    @Transactional
    @Override
    public void postProcessEntity(IdentifiableEntity<?> entity, Object dto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException {
        resolver.setResolvedEntities(entity,dto,fieldsToCheck);
    }

}
