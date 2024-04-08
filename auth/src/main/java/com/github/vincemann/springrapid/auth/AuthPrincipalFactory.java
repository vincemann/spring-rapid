package com.github.vincemann.springrapid.auth;

public interface AuthPrincipalFactory {
    AuthPrincipal create(AbstractUser<?> user);
}
