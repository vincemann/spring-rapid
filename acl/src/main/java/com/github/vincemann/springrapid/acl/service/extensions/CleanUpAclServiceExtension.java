package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.core.service.security.MockAuthService;
import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
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
public class CleanUpAclServiceExtension
        extends AbstractAclServiceExtension<SimpleCrudService>
                implements SimpleCrudServiceExtension<SimpleCrudService>
{
    @Setter
    private boolean deleteCascade = true;

    public CleanUpAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
        deleteAcl(id,getEntityClass());
    }

//    @CalledByProxy
//    public void onAfterDeleteById(Serializable id,Class entityClass) throws EntityNotFoundException, BadEntityException {
//
//    }

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
