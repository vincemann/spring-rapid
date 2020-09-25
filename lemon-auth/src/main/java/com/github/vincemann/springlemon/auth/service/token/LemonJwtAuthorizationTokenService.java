package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;
import com.github.vincemann.springlemon.auth.util.LemonValidationUtils;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Service
public class LemonJwtAuthorizationTokenService extends AbstractJwtAuthorizationTokenService<LemonAuthenticatedPrincipal> {

    private SimpleUserService userService;


    @Transactional
    @Override
    public void verifyToken(JWTClaimsSet claims, LemonAuthenticatedPrincipal principal) {
        super.verifyToken(claims, principal);
        try {
            AbstractUser<?> byEmail = userService.findByEmail(principal.getEmail());
            LemonValidationUtils.ensureCredentialsUpToDate(claims,byEmail);
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("User encoded in token not found",e);
        }
    }

    @Autowired
    public void injectUserService(SimpleUserService userService) {
        this.userService = userService;
    }
}
