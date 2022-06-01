package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@WebComponent
@Slf4j
public class RapidJwtAuthorizationTokenService extends AbstractJwtAuthorizationTokenService<RapidAuthAuthenticatedPrincipal> {

    private UserService userService;


    @Transactional
    @Override
    public void verifyToken(JWTClaimsSet claims, RapidAuthAuthenticatedPrincipal principal) {
        super.verifyToken(claims, principal);
        try {
            Optional<AbstractUser<?>> byContactInformation = userService.findByContactInformation(principal.getContactInformation());
            VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+principal.getContactInformation()+" not found");
            AbstractUser<?> user = byContactInformation.get();
            RapidJwt.validateIssuedAfter(claims,user.getCredentialsUpdatedMillis());
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("User encoded in token not found",e);
        }
    }

    @Lazy
    @Autowired
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }
}
