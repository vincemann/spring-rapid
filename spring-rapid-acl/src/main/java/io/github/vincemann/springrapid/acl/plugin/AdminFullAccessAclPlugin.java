package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Default Acl Plugin, granting admin permission for creator and admin of entity saved by {@link io.github.vincemann.springrapid.core.service.CrudService}.
 * Acl Info gets deleted, when entity gets deleted.
 * @param <E>
 * @param <Id>
 */
@Slf4j
@Getter
@ServiceComponent
public abstract class AdminFullAccessAclPlugin<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends CleanUpAclPlugin<E,Id> {

    @Value("${rapid.acl.adminRole:ROLE_ADMIN}")
    private String adminRole;


    public AdminFullAccessAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }

    @Transactional
    @CalledByProxy
    public void onAfterSave(E requestEntity, E returnedEntity) {
        log.debug("admin now gets full permission over entity: " + returnedEntity);
        saveFullPermissionForAdminOver(returnedEntity);
    }



    protected void saveFullPermissionForAdminOver(E entity){
        getPermissionService().addPermissionForAuthorityOver(entity,BasePermission.ADMINISTRATION, adminRole);
    }

}
