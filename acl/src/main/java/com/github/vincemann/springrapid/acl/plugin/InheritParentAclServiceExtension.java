package com.github.vincemann.springrapid.acl.plugin;


import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * On {@link com.github.vincemann.springrapid.core.service.CrudService#save(IdentifiableEntity)} the Permissions (@see {@link org.springframework.security.acls.domain.BasePermission})
 * from the Acl-Parent, retrieved via {@link AclParentAware#getAclParent()}, will be inherited.

 */
@Transactional
public class InheritParentAclServiceExtension<E extends IdentifiableEntity<Id> & AclParentAware,Id extends Serializable>
        extends AbstractAclServiceExtension<CrudService<E,Id,?>,E,Id> {

    public InheritParentAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        E saved = super.save(entity);
        getPermissionService().inheritPermissions(saved,saved.getAclParent());
        return saved;
    }

//    @Transactional
//    @CalledByProxy
//    public void onAfterSave(E requestEntity, E returnedEntity) throws AclNotFoundException {
//
//    }


}
