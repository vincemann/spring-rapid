package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import org.springframework.security.acls.model.MutableAclService;

public class AuthenticatedFullAccessAclPluginImpl extends AuthenticatedFullAccessAclPlugin {
    public AuthenticatedFullAccessAclPluginImpl(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }
}
