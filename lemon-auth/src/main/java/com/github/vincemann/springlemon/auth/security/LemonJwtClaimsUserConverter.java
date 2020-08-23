package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.HashMap;
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
            throw new AuthenticationCredentialsNotFoundException("subject of claims-set not found");
        try {
            return lemonService.findByEmail(email);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded email: " + email + " does not exist.",e);
        }
    }

    //issuer will always be users email
    @Override
    public Map<String, Object> toClaimsPayload(AbstractUser user) {
//        Assert.notNull(user.getEmail(),"email");
//        return LecUtils.mapOf(SUBJECT_CLAIMS_KEY,user.getEmail());
        return new HashMap<>();
    }
}
