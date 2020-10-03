package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

//working with unknown id here bc user can just use IdConverter-Bean if he needs real id type
public interface AuthenticatedPrincipalFactory<P extends LemonAuthenticatedPrincipal,U extends AbstractUser<?>> {
    public P create(U user);
}
