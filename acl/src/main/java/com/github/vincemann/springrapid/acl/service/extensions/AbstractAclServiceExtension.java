package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.Role;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * Base class for acl info managing {@link ServiceExtension}s.
 *
 * @param <S>
 */
@AllArgsConstructor
@Getter
public abstract class AbstractAclServiceExtension<S>
        extends ServiceExtension<S> {

    private LocalPermissionService permissionService;
    private MutableAclService mutableAclService;
    private MockAuthService mockAuthService;

    @LogInteraction(Severity.TRACE)
    protected void saveFullPermissionForAdminOver(IdentifiableEntity<Serializable> entity){
        mockAuthService.runAuthenticatedAsAdmin(() -> {
            getPermissionService().addPermissionForAuthorityOver(entity,
                    BasePermission.ADMINISTRATION, Role.ADMIN);
        });
    }

    @LogInteraction(Severity.TRACE)
    protected void savePermissionForAuthenticatedOver(IdentifiableEntity<Serializable> entity, Permission permission){
        String own = findAuthenticatedName();
        //not needed, acl, SecurityContext already has right name...
//        mockAuthService.runAuthenticatedAs(own,() -> {
            getPermissionService().addPermissionForUserOver(entity, permission,own);
//        });

    }

    protected String findAuthenticatedName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Nicht auslagern. MutableAclService macht das intern auch so -> use @MockUser(username="testUser") in tests
        if(authentication==null){
            throw new IllegalArgumentException("Authentication required");
        }
        return authentication.getName();
    }
}
