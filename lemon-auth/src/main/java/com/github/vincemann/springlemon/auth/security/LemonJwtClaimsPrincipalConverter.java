package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Only stores email in token and fetches user args for principal lazily
 */
@Transactional
public class LemonJwtClaimsPrincipalConverter implements JwtClaimsPrincipalConverter<LemonAuthenticatedPrincipal> {

    private LemonService<AbstractUser<?>, ?, ?> lemonService;

    @Autowired
    public LemonJwtClaimsPrincipalConverter(LemonService<AbstractUser<?>, ?, ?> lemonService) {
        this.lemonService = lemonService;
    }

    @Override
    public JWTClaimsSet toClaims(LemonAuthenticatedPrincipal user) {
        return new JWTClaimsSet.Builder()
                .subject(user.getName())
                .build();
    }


    @Override
    public LemonAuthenticatedPrincipal toPrincipal(JWTClaimsSet claims) throws AuthenticationCredentialsNotFoundException {
        String email = claims.getSubject();
        if (email == null)
            throw new AuthenticationCredentialsNotFoundException("subject of claims-set not found");
        try {
            AbstractUser<?> user = lemonService.findByEmail(email);
            return new LemonAuthenticatedPrincipal(user.getEmail(), user.getPassword(), user.getRoles());
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded email: " + email + " does not exist.", e);
        }
    }


}
