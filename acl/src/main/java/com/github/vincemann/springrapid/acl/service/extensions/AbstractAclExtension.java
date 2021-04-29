package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.Roles;
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
public abstract class AbstractAclExtension<S>
        extends BasicServiceExtension<S> {

    private LocalPermissionService permissionService;
    private MutableAclService mutableAclService;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;



    @LogInteraction(Severity.TRACE)
    public void saveFullPermissionForAdminOverEntity(IdentifiableEntity<?> entity){
        //acl framework uses internally springs Authentication object
        securityContext.runAsAdmin(() -> getPermissionService().addPermissionForAuthorityOver(entity,
                BasePermission.ADMINISTRATION, Roles.ADMIN));
    }

    @LogInteraction(Severity.TRACE)
    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission){
        String own = findAuthenticatedName();
        getPermissionService().addPermissionForUserOver(entity, permission,own);
    }


    @LogInteraction(Severity.TRACE)
    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission){
        securityContext.runWithName(user,() -> getPermissionService().addPermissionForUserOver(entity, permission,user));
    }

    public String findAuthenticatedName(){
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
