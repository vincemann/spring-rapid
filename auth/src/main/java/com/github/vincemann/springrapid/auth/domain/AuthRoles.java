package com.github.vincemann.springrapid.auth.domain;

import com.github.vincemann.springrapid.core.security.Roles;

public interface AuthRoles extends Roles {
    String UNVERIFIED = "ROLE_UNVERIFIED";
    String UNVERIFIED_RAW = "UNVERIFIED";

    String BLOCKED = "ROLE_BLOCKED";
    String BLOCKED_RAW = "BLOCKED";
}
