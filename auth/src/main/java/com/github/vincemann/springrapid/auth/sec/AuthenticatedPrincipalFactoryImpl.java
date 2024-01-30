package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;

public class AuthenticatedPrincipalFactoryImpl implements AuthenticatedPrincipalFactory {


    @Override
    public RapidPrincipal create(AbstractUser<?> user) {
       return new RapidPrincipal(user.getContactInformation(),user.getPassword(),user
                .getRoles(),user.getId() == null ? null : user.getId().toString());
    }

}
