package com.github.vincemann.springrapid.acl.proxy.rules;


import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.acl.proxy.ServiceSecurityProxy;
import com.github.vincemann.springrapid.core.proxy.CrudServicePlugin;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@ServiceComponent
/**
 * Base Class for Rules applied by {@link ServiceSecurityProxy}.
 * Naming Convention for defining hook methods:
 *
 * void onBeforeMyServiceMethod(all,args,in,service,method,Class<ServiceEntityType>)
 * void onAfterMyServiceMethod(all,args,serviceResult, Class<ServiceEntityType>)
 * T onAfterMyServiceMethod(...)        -> return value of hooked service methods gets updated with return value from hook method.
 * void onAfterMyServiceMethod(...)     -> return value of hooked service method is not tampered with
 *
 */
public abstract class ServiceSecurityRule extends CrudServicePlugin{
    private String readPermission = "READ";
    private String writePermission = "WRITE";
    private String createPermission = "CREATE";
    private String deletePermission = "DELETE";
    private String administrationPermission = "ADMINISTRATION";

    private AclSecurityChecker securityChecker;

    protected String getReadPermission() {
        return readPermission;
    }

    protected void setReadPermission(String readPermission) {
        this.readPermission = readPermission;
    }

    protected String getWritePermission() {
        return writePermission;
    }

    protected void setWritePermission(String writePermission) {
        this.writePermission = writePermission;
    }

    protected String getDeletePermission() {
        return deletePermission;
    }

    protected void setDeletePermission(String deletePermission) {
        this.deletePermission = deletePermission;
    }

    protected AclSecurityChecker getSecurityChecker() {
        return securityChecker;
    }

    public void setSecurityChecker(AclSecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
