package com.github.vincemann.springrapid.acl.framework.oidresolve;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Extracts {@link IdentifiableEntity} encoded by {@link ObjectIdentity} from Database.
 */
public class RapidObjectIdentityResolver implements ObjectIdentityResolver {

    private CrudServiceLocator crudServiceLocator;
    private IdConverter idConverter;


    @Transactional
    public <T extends IdentifiableEntity<?>> T resolve(ObjectIdentity objectIdentity) throws UnresolvableOidException {
        try {
            Class<?> clazz = Class.forName(objectIdentity.getType());
            if (!IdentifiableEntity.class.isAssignableFrom(clazz)) {
                throw new UnresolvableOidException("Oid has unknown entity class: " + clazz);
            }
            Class<? extends IdentifiableEntity> entityClass = (Class<? extends IdentifiableEntity>) clazz;
            CrudService crudService = crudServiceLocator.find(entityClass);
            if (crudService == null) {
                throw new UnresolvableOidException("Entity class: " + entityClass + " does not map to service.");
            }
            return (T) VerifyEntity.isPresent(
                    crudService.findById(idConverter.toId(objectIdentity.getIdentifier().toString())),
                    objectIdentity.getIdentifier(),
                    entityClass);

        } catch (ClassNotFoundException | EntityNotFoundException | ClassCastException e) {
            throw new UnresolvableOidException(e);
        }
    }

    @Autowired
    public void injectCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    @Autowired
    public void injectIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }
}
