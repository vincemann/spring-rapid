package com.github.vincemann.springrapid.acl.service.extensions;


import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * On {@link com.github.vincemann.springrapid.core.service.CrudService#save(E)} the Permissions (@see {@link org.springframework.security.acls.domain.BasePermission})
 * from the Acl-Parent, retrieved via {@link AclParentAware#getAclParent()}, will be inherited.

 */
@Transactional
public class InheritParentAclServiceExtension<E extends IdentifiableEntity<Id> & AclParentAware,Id extends Serializable>
        extends AbstractAclServiceExtension<SimpleCrudService<E,Id>>
                                 implements SimpleCrudService<E,Id> {

    public InheritParentAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @Override
    public Optional<E> findById(Id id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    public E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        E saved = getNext().save(entity);
        getPermissionService().inheritPermissions(saved,saved.getAclParent());
        return saved;
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
    }

    @Override
    public Set<E> findAll() {
        return getNext().findAll();
    }

    @Override
    public Class<E> getEntityClass() {
        return getNext().getEntityClass();
    }


    

//    @Transactional
//    @CalledByProxy
//    public void onAfterSave(E requestEntity, E returnedEntity) throws AclNotFoundException {
//
//    }


}
