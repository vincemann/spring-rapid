package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springlemon.demo.domain.User;

@ServiceComponent
public class MyLemonAuthenticatedPrincipalFactory extends LemonAuthenticatedPrincipalFactory<User> {
}
