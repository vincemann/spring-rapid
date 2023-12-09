package com.github.vincemann.springrapid.acl.service.extensions.security;

import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.domain.BasePermission;

import java.io.Serializable;
import java.util.Optional;


public class NeedCreatePermissionOnParentForSaveExtension
        extends AbstractSecurityExtension<CrudService>
        implements CrudServiceExtension<CrudService> {


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        if (entity instanceof AclParentAware){
            IdentifiableEntity<? extends Serializable> aclParent = ((AclParentAware) entity).getAclParent();
            if (aclParent == null){
                throw new BadEntityException("No acl parent found");
            }else {
                getSecurityChecker().checkPermission(aclParent, BasePermission.CREATE);
            }
        }
        return getNext().save(entity);
    }

    @Override
    public Optional findById(Serializable id) throws BadEntityException {
        getSecurityChecker().checkPermission(id,getLast().getEntityClass(), BasePermission.READ);
        return getNext().findById(id);
    }
}
