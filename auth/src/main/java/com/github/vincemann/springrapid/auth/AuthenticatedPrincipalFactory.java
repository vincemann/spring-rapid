package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthPrincipal;

public interface AuthenticatedPrincipalFactory {
    AuthPrincipal create(AbstractUser<?> user);
}
