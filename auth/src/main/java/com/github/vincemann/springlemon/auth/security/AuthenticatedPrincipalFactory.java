package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.RapidAuthAuthenticatedPrincipal;

//working with unknown id here bc user can just use IdConverter-Bean if he needs real id type
public interface AuthenticatedPrincipalFactory<P extends RapidAuthAuthenticatedPrincipal,U extends AbstractUser<?>> {
    public P create(U user);
}
