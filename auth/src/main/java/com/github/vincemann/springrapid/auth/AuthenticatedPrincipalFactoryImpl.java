package com.github.vincemann.springrapid.auth;

public class AuthenticatedPrincipalFactoryImpl implements AuthenticatedPrincipalFactory {


    @Override
    public AuthPrincipal create(AbstractUser<?> user) {
       return new AuthPrincipal(user.getContactInformation(),user.getPassword(),user
                .getRoles(),user.getId() == null ? null : user.getId().toString());
    }

}
