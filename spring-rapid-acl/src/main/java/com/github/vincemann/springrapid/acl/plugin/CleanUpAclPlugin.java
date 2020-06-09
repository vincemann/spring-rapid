package com.github.vincemann.springrapid.acl.plugin;

import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Getter
@Slf4j
/**
 * Removes Acl's on delete, if existing.
 */
@Transactional
public class CleanUpAclPlugin
    extends AbstractAclPlugin
{

    @Setter
    private boolean deleteCascade = true;

    public CleanUpAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @CalledByProxy
    public void onAfterDeleteById(Serializable id,Class entityClass) throws EntityNotFoundException, BadEntityException {
        deleteAcl(id,entityClass);
    }

    private void deleteAcl(Serializable id, Class entityClass){
        log.debug("deleting acl for entity with id: " + id + " and class: " + entityClass);
        //delete acl as well
        ObjectIdentity oi = new ObjectIdentityImpl(entityClass, id);
        log.debug("ObjectIdentity getting deleted: " + oi);
        //todo delete children ist nur richtig wenn ich wirklich one to n habe mit Delete Cascade!
        getMutableAclService().deleteAcl(oi,deleteCascade);
        log.debug("Acl successfully deleted");
    }

}
