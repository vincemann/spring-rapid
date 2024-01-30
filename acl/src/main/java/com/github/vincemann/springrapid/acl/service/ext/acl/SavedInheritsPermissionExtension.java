package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.AceFilter;
import com.github.vincemann.springrapid.acl.service.AclNotFoundException;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Transactional
@Getter
public class SavedInheritsPermissionExtension<E extends IdentifiableEntity<Id> & AclParentAware, Id extends Serializable>
        extends AclExtension<CrudService<E, Id>>
        implements GenericCrudServiceExtension<CrudService<E, Id>, E, Id> {

    private Permission[] permissionsToInherit;

    public SavedInheritsPermissionExtension(Permission... permissions) {
        this.permissionsToInherit = permissions;
    }

    @LogInteraction
    @Override
    public E save(E entity) throws BadEntityException {
        E saved = getNext().save(entity);
        try {
            getAclPermissionService().copyParentAces(saved, saved.getAclParent(),
                    AceFilter.builder()
                            .permissions(permissionsToInherit)
                            .build());
        } catch (AclNotFoundException e) {
            throw new BadEntityException("Cant find acl info of parent to inherit from", e);
        }
        return saved;
    }
}
