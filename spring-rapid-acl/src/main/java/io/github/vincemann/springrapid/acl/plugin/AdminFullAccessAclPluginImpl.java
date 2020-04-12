package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.security.acls.model.MutableAclService;

/**
 * Implementation of {@link AdminFullAccessAclPlugin}.
 * Use this as an Acl Plugin, if you do not want to build upon the functionality in {@link AdminFullAccessAclPlugin}.
 */
@ServiceComponent
public class AdminFullAccessAclPluginImpl extends AdminFullAccessAclPlugin {
    public AdminFullAccessAclPluginImpl(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }
}
