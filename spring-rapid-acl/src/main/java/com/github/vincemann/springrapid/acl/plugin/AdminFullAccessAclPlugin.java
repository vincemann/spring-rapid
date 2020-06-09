package com.github.vincemann.springrapid.acl.plugin;

import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Abstract Acl Plugin, granting {@link BasePermission#ADMINISTRATION} for creator and admin(role) of entity saved by {@link com.github.vincemann.springrapid.core.service.CrudService}.
 */
@Slf4j
@Getter
public class AdminFullAccessAclPlugin
        extends AbstractAclPlugin {


    public AdminFullAccessAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService) {
        super(permissionService, mutableAclService, mockAuthService);
    }

    @Transactional
    @CalledByProxy
    public void onAfterSave(IdentifiableEntity<Serializable> requestEntity, IdentifiableEntity<Serializable> returnedEntity) {
        log.debug("admin now gets full permission over entity: " + returnedEntity);
        saveFullPermissionForAdminOver(returnedEntity);
    }

}
