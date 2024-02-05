package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
public class JwtAuthorizationTokenServiceImpl extends AbstractJwtAuthorizationTokenService {

    private UserService userService;


    @Transactional
    @Override
    public void verifyToken(JWTClaimsSet claims, RapidPrincipal principal) {
        super.verifyToken(claims, principal);
        try {
            Optional<AbstractUser<?>> byContactInformation = userService.findByContactInformation(principal.getName());
            VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+principal.getName()+" not found");
            AbstractUser<?> user = byContactInformation.get();
            RapidJwt.validateIssuedAfter(claims,user.getCredentialsUpdatedMillis());
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("User encoded in token not found",e);
        }
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
