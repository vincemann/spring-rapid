package io.github.vincemann.springrapid.acl.plugin;

import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.security.acls.model.MutableAclService;

@ServiceComponent
public class AdminFullAccessAclPluginImpl extends AdminFullAccessAclPlugin {
    public AdminFullAccessAclPluginImpl(LocalPermissionService permissionService, MutableAclService mutableAclService) {
        super(permissionService, mutableAclService);
    }
}
