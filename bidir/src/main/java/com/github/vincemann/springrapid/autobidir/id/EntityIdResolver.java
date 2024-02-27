package com.github.vincemann.springrapid.autobidir.id;


import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Optional;

@Getter
/**
 *
 *  Baseclass for components resolving entity to id and vice versa
 *

 *
 *  The resolving of the ids is done, by calling {@link CrudService#findById(Serializable)} of the {@link CrudService},
 *  that belongs to the Annotated Id's Entity Type.
 *  The needed CrudService is found with {@link CrudServiceLocator}.
 *
 * @see com.github.vincemann.springrapid.autobidir.id.biDir.BiDirChildIdResolver
 * @see RelationalDtoManagerImpl
 */
public interface EntityIdResolver {


    boolean supports(Class<?> dtoClass);

    /**
     * Resolve entities by id from dto and inject (set) them into target Entity
     * -> target entity now has all entities set
     */
    void setResolvedEntities(IdentifiableEntity mappedEntity, Object dto, String... fieldsToCheck) throws BadEntityException, EntityNotFoundException;

    /**
     * Resolve Id's from entities in entity and inject (set) ids into target Dto
     */
    void setResolvedIds(Object mappedDto, IdentifiableEntity entity, String... fieldsToCheck);



}
