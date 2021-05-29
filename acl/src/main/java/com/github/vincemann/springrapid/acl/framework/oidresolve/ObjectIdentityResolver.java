package com.github.vincemann.springrapid.acl.framework.oidresolve;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.ObjectIdentity;

public interface ObjectIdentityResolver {
    public <T extends IdentifiableEntity<?>> T resolve(ObjectIdentity objectIdentity) throws UnresolvableOidException;
}
