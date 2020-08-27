package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class AuthenticatedFullAccessAclServiceExtension
        extends AbstractAclServiceExtension<SimpleCrudService>
                implements SimpleCrudServiceExtension<SimpleCrudService> {


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        savePermissionForAuthenticatedOver(saved, BasePermission.ADMINISTRATION);
        return saved;
    }

//    @CalledByProxy
//    public void onAfterSave(IdentifiableEntity<Serializable> requestEntity, IdentifiableEntity<Serializable> returnedEntity) {
//
//    }

}
