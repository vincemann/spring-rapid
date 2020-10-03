package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.security.RapidRoles;

public interface LemonRoles extends RapidRoles {
    String UNVERIFIED = "ROLE_UNVERIFIED";
    String UNVERIFIED_RAW = "UNVERIFIED";

    String BLOCKED = "ROLE_BLOCKED";
    String BLOCKED_RAW = "BLOCKED";

    String ANON = "ROLE_ANONYMOUS";
    String ANON_RAW = "ANONYMOUS";
}
