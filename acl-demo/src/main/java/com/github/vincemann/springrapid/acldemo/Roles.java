package com.github.vincemann.springrapid.acldemo;

import com.github.vincemann.springrapid.auth.model.AuthRoles;

public interface Roles extends AuthRoles {
    String NEW_VET = "ROLE_NEW_VET";
    String VET = "ROLE_VET";

    String OWNER = "ROLE_OWNER";
}
