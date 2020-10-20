package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.util.JwtUtils;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@ServiceComponent
@Service
public class LemonJwtAuthorizationTokenService extends AbstractJwtAuthorizationTokenService<LemonAuthenticatedPrincipal> {

    private UserService unsecuredUserService;


    @Transactional
    @Override
    public void verifyToken(JWTClaimsSet claims, LemonAuthenticatedPrincipal principal) {
        super.verifyToken(claims, principal);
        try {
            Optional<AbstractUser<?>> byEmail = unsecuredUserService.findByEmail(principal.getEmail());
            VerifyEntity.isPresent(byEmail,"User with email: "+principal.getEmail()+" not found");
            AbstractUser<?> user = byEmail.get();
            JwtUtils.ensureCredentialsUpToDate(claims,user);
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("User encoded in token not found",e);
        }
    }

    @Lazy
    @Unsecured
    @Autowired
    public void injectUnsecuredUserService(UserService userService) {
        this.unsecuredUserService = userService;
    }
}
