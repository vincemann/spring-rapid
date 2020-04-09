package io.github.vincemann.springrapid.acl.plugin;


import io.github.vincemann.springrapid.acl.model.AclParentAware;
import io.github.vincemann.springrapid.acl.service.AclNotFoundException;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@ServiceComponent
public class InheritParentAclPlugin<E extends IdentifiableEntity<Id> & AclParentAware,Id extends Serializable>
        extends CleanUpAclPlugin<E,Id> {

    public InheritParentAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }

    @Transactional
    @CalledByProxy
    public void onAfterSave(E requestEntity, E returnedEntity) throws AclNotFoundException {
        getPermissionService().inheritPermissions(returnedEntity,returnedEntity.getAclParent());
    }


}
