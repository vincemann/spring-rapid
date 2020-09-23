package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidRoles;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

/**
 * Base class for acl info managing {@link BasicServiceExtension}s.
 *
 * @param <S>
 */
@Getter
public abstract class AbstractAclServiceExtension<S>
        extends BasicServiceExtension<S> {

    private LocalPermissionService permissionService;
    private MutableAclService mutableAclService;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;



    @LogInteraction(Severity.TRACE)
    protected void saveFullPermissionForAdminOver(IdentifiableEntity<Serializable> entity){
        //acl framework uses internally springs Authentication object
        securityContext.runAsAdmin(() -> getPermissionService().addPermissionForAuthorityOver(entity,
                BasePermission.ADMINISTRATION, RapidRoles.ADMIN));
    }

    @LogInteraction(Severity.TRACE)
    protected void savePermissionForAuthenticatedOver(IdentifiableEntity<Serializable> entity, Permission permission){
        String own = findAuthenticatedName();
        getPermissionService().addPermissionForUserOver(entity, permission,own);
    }

    protected void savePermissionForUserOver(String user, IdentifiableEntity<Serializable> entity, Permission permission){
        securityContext.runWithName(user,() -> getPermissionService().addPermissionForUserOver(entity, permission,user));
    }

    protected String findAuthenticatedName(){
        String name = RapidSecurityContext.getName();
        //Nicht auslagern. MutableAclService macht das intern auch so -> use @MockUser(username="testUser") in tests
        if(name==null){
            throw new IllegalArgumentException("Authentication required");
        }
        return name;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }
    @Autowired
    public void injectPermissionService(LocalPermissionService permissionService) {
        this.permissionService = permissionService;
    }
    @Autowired
    public void injectMutableAclService(MutableAclService mutableAclService) {
        this.mutableAclService = mutableAclService;
    }
}
