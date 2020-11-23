package com.github.vincemann.springrapid.acl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AclProperties {

    public AclProperties() {
    }

    public boolean defaultAclChecks = true;
}
