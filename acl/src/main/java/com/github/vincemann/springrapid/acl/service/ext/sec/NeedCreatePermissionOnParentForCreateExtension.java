package com.github.vincemann.springrapid.acl.service.ext.sec;

import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.util.Assert;

import java.io.Serializable;


public class NeedCreatePermissionOnParentForCreateExtension
        <E extends IdentifiableEntity<Id> & AclParentAware, Id extends Serializable>
        extends SecurityExtension<CrudService<E,Id>>
        implements GenericCrudServiceExtension<CrudService<E,Id>,E,Id> {


    @Override
    public E create(E entity) throws BadEntityException {
        IdentifiableEntity<? extends Serializable> aclParent = entity.getAclParent();
        Assert.notNull(aclParent,"acl parent not found");
        getAclTemplate().checkPermission(aclParent, BasePermission.CREATE);
        return getNext().create(entity);
    }
}
