package com.github.vincemann.springrapid.acldemo.auth;

import com.github.vincemann.springrapid.auth.model.AuthRoles;

public interface MyRoles extends AuthRoles {
    String NEW_VET = "ROLE_NEW_VET";
    String VET = "ROLE_VET";

    String OWNER = "ROLE_OWNER";
}
