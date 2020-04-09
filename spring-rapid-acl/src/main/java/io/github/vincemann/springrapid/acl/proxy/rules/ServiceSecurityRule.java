package io.github.vincemann.springrapid.acl.proxy.rules;


import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@ServiceComponent
/**
 * Base Class for Rules applied by {@link io.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy}.
 * Naming Convention for defining hook methods:
 *
 * void onBeforeMyServiceMethod(all,args,in,service,method,Class<ServiceEntityType>)
 * void onAfterMyServiceMethod(all,args,serviceResult, Class<ServiceEntityType>)
 * T onAfterMyServiceMethod(...) -> return value of hooked service methods gets updated with return value from hook method.
 *
 *
 */
public abstract class ServiceSecurityRule extends CrudServicePlugin{
    private String readPermission = "READ";
    //@Value("${crud.security.writePermission:WRITE}")
    private String writePermission = "WRITE";
    //@Value("${crud.security.createPermission:CREATE}")
    private String createPermission = "CREATE";
    //@Value("${crud.security.deletePermission:DELETE}")
    private String deletePermission = "DELETE";

    private SecurityChecker securityChecker;

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

    protected SecurityChecker getSecurityChecker() {
        return securityChecker;
    }

    public void setSecurityChecker(SecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
