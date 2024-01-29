package com.github.vincemann.springrapid.auth.model;

import com.github.vincemann.springrapid.core.sec.Roles;

public interface AuthRoles extends Roles {
    String UNVERIFIED = "ROLE_UNVERIFIED";
    String UNVERIFIED_RAW = "UNVERIFIED";

    String BLOCKED = "ROLE_BLOCKED";
    String BLOCKED_RAW = "BLOCKED";
}
