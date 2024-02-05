package com.github.vincemann.springrapid.acl.service.ext.acl;


import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.AceFilter;
import com.github.vincemann.springrapid.acl.service.AclNotFoundException;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * On {@link com.github.vincemann.springrapid.core.service.AbstractCrudService#create(E)} the Permissions (@see {@link org.springframework.security.acls.domain.BasePermission})
 * from the Acl-Parent, retrieved via {@link AclParentAware#getAclParent()}, will be inherited.

 */
@Transactional
public class CreatedInheritsParentsPermissionsAclExtension<E extends IdentifiableEntity<Id> & AclParentAware,Id extends Serializable>
                        extends AclExtension<CrudService<E,Id>>
                                 implements GenericCrudServiceExtension<CrudService<E,Id>,E,Id> {

    @LogInteraction
    @Override
    public E create(E entity) throws BadEntityException {
        E saved = getNext().create(entity);
        try {
            getRapidAclService().copyParentAces(saved,saved.getAclParent(), AceFilter.noFilter());
        } catch (AclNotFoundException e) {
            throw new BadEntityException("Cant find acl info of parent to inherit from",e);
        }
        return saved;
    }

}
