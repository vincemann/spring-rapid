package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.AceFilter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CreatedInheritsPermissionsFromParentExtension<E extends IdentifiableEntity<Id> & AclParentAware, Id extends Serializable>
        extends AclExtension<CrudService<E, Id>>
        implements GenericCrudServiceExtension<CrudService<E, Id>, E, Id> {

    private Permission[] permissionsToInherit;

    public CreatedInheritsPermissionsFromParentExtension(Permission... permissions) {
        this.permissionsToInherit = permissions;
    }

    @Transactional
    @LogInteraction
    @Override
    public E create(E entity) throws BadEntityException {
        E saved = getNext().create(entity);
        Assert.notNull(saved.getAclParent(),"acl parent cant be null");
        getRapidAclService().copyParentAces(saved, saved.getAclParent(),
                AceFilter.builder()
                        .permissions(permissionsToInherit)
                        .build());
        return saved;
    }

}
