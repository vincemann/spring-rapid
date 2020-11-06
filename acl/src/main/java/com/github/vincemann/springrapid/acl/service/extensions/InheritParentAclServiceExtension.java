package com.github.vincemann.springrapid.acl.service.extensions;


import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * On {@link com.github.vincemann.springrapid.core.service.AbstractCrudService#save(E)} the Permissions (@see {@link org.springframework.security.acls.domain.BasePermission})
 * from the Acl-Parent, retrieved via {@link AclParentAware#getAclParent()}, will be inherited.

 */
@Transactional
@LogInteraction(Severity.TRACE)
public class InheritParentAclServiceExtension<E extends IdentifiableEntity<Id> & AclParentAware,Id extends Serializable>
                        extends AbstractAclServiceExtension<CrudService<E,Id>>
                                 implements GenericCrudServiceExtension<CrudService<E,Id>,E,Id> {


    @Override
    public E save(E entity) throws BadEntityException {
        E saved = getNext().save(entity);
        getPermissionService().inheritPermissions(saved,saved.getAclParent());
        return saved;
    }

}
