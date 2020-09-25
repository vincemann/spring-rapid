package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public class LemonPrincipalUserConverter implements PrincipalUserConverter<LemonAuthenticatedPrincipal, AbstractUser<?>> {

    private SimpleUserService userService;

    @Override
    public LemonAuthenticatedPrincipal toPrincipal(AbstractUser<?> user) {
        return new LemonAuthenticatedPrincipal(user);
    }

    @Override
    public AbstractUser<?> toUser(LemonAuthenticatedPrincipal principal) throws EntityNotFoundException {
        return userService.findByEmail(principal.getEmail());
    }

    @Autowired
    public void injectUserService(SimpleUserService userService) {
        this.userService = userService;
    }
}
