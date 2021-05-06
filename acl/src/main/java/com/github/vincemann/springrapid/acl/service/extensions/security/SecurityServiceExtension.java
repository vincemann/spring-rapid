package com.github.vincemann.springrapid.acl.service.extensions.security;

import com.github.vincemann.springrapid.acl.AclSecurityChecker;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Write extensions for secured Services
 * @see com.github.vincemann.springrapid.core.proxy.BasicServiceExtension
 */
@Getter
public class SecurityServiceExtension<T>
        extends BasicServiceExtension<T> {
    private String readPermission = "READ";
    private String writePermission = "WRITE";
    private String createPermission = "CREATE";
    private String deletePermission = "DELETE";
    private String administrationPermission = "ADMINISTRATION";

    private AclSecurityChecker securityChecker;

    @Autowired
    public void injectAclSecurityChecker(AclSecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }

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

}
