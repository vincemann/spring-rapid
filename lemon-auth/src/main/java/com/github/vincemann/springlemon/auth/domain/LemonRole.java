package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.security.RapidRole;

public interface LemonRole extends RapidRole {
    String UNVERIFIED = "ROLE_UNVERIFIED";
    String BLOCKED = "ROLE_BLOCKED";
    String GOOD_ADMIN = "ROLE_GOOD_ADMIN";
    String GOOD_ADMIN_RAW = "GOOD_ADMIN";
    String GOOD_USER = "ROLE_GOOD_USER";
    String GOOD_USER_RAW = "GOOD_USER";
}
