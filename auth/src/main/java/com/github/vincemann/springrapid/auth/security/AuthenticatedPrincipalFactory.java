package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthAuthenticatedPrincipalImpl;

//working with unknown id here bc user can just use IdConverter-Bean if he needs real id type
public interface AuthenticatedPrincipalFactory<P extends AuthAuthenticatedPrincipalImpl,U extends AbstractUser<?>> {
    public P create(U user);
}
