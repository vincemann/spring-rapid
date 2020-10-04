package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Only stores email in token and fetches user args for principal lazily
 */
@Transactional
public class LemonJwtClaimsPrincipalConverter
            implements JwtClaimsPrincipalConverter<LemonAuthenticatedPrincipal> {

    private UserService unsecuredUserService;


    @Override
    public Map<String,Object> toClaims(LemonAuthenticatedPrincipal user) {
        return LemonMapUtils.mapOf("email",user.getEmail());
    }


    @Override
    public LemonAuthenticatedPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException {
        String email = (String) claims.get("email");
        if (email == null)
            throw new AuthenticationCredentialsNotFoundException("email claim of claims-set not found");
        try {
            Optional<AbstractUser<?>> byEmail = unsecuredUserService.findByEmail(email);
            VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
            AbstractUser<?> user = byEmail.get();
            return new LemonAuthenticatedPrincipal(user);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded email: " + email + " does not exist.", e);
        }
    }

    @Unsecured
    @Autowired
    public void injectUnsecuredUserService(UserService userService) {
        this.unsecuredUserService = userService;
    }
}
