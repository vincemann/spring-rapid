package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.LemonMapUtils;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Only stores email in token and fetches user args for principal lazily
 */
@Transactional
public class RapidJwtClaimsPrincipalConverter
            implements JwtClaimsPrincipalConverter<RapidAuthAuthenticatedPrincipal> {

    private UserService userService;


    @Override
    public Map<String,Object> toClaims(RapidAuthAuthenticatedPrincipal user) {
        return LemonMapUtils.mapOf("email",user.getEmail());
    }


    @Override
    public RapidAuthAuthenticatedPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException {
        String email = (String) claims.get("email");
        if (email == null)
            throw new AuthenticationCredentialsNotFoundException("email claim of claims-set not found");
        try {
            Optional<AbstractUser<?>> byEmail = userService.findByEmail(email);
            VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
            AbstractUser<?> user = byEmail.get();
            return new RapidAuthAuthenticatedPrincipal(user);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded email: " + email + " does not exist.", e);
        }
    }

    @Lazy

    @Autowired
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }
}
