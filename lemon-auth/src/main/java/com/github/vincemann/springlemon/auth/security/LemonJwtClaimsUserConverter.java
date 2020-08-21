package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.util.LecUtils;
import com.github.vincemann.springrapid.commons.Assert;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.security.AbstractAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;

public class LemonJwtClaimsUserConverter implements JwtClaimsUserConverter {

    private LemonService<AbstractUser<?>,?,?> lemonService;

    @Autowired
    public LemonJwtClaimsUserConverter(LemonService<AbstractUser<?>, ?, ?> lemonService) {
        this.lemonService = lemonService;
    }

    @Override
    public AbstractUser toUser(JWTClaimsSet claims) throws AuthenticationCredentialsNotFoundException {
        String email = claims.getSubject();
        if (email == null)
            throw new AuthenticationCredentialsNotFoundException("claim with key: " + AuthorizationTokenService.USER_EMAIL_CLAIM + " not found");
        try {
            return lemonService.findByEmail(email);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded email: " + email + " does not exist.",e);
        }
    }

    @Override
    public Map<String, Object> toClaimsPayload(AbstractUser user) {
//        Assert.notNull(user.getEmail(),"email");
        return LecUtils.mapOf(SUBJECT_CLAIMS_KEY,user.getEmail());
    }
}
