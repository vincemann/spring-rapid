package com.github.vincemann.springrapid.auth.jwt;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;
import java.util.Optional;

import static com.github.vincemann.springrapid.auth.util.JwtUtils.AUTH_CLAIM;
import static com.github.vincemann.springrapid.auth.util.JwtUtils.create;

public class JwtAuthorizationTokenService
        implements AuthorizationTokenService {

    private static final String PRINCIPAL_CLAIMS_KEY = "rapid-principal";


    private JwsTokenService jwsTokenService;
    private JwtPrincipalConverter jwtPrincipalConverter;
    private AuthProperties properties;

    private UserService userService;


    @Override
    public String createToken(AuthPrincipal principal) {
        Map<String, Object> principalClaims = jwtPrincipalConverter.toClaims(principal);
        JWTClaimsSet claims = create(AUTH_CLAIM,
                principal.getName(),
                properties.getJwt().getExpirationMillis(),
                MapUtils.mapOf(PRINCIPAL_CLAIMS_KEY, principalClaims)
        );

        return jwsTokenService.createToken(claims);
    }

    @Override
    public AuthPrincipal parseToken(String token) throws BadTokenException {
        JWTClaimsSet jwtClaimsSet = jwsTokenService.parseToken(token);
        Map<String, Object> principalClaims = (Map<String, Object>) jwtClaimsSet.getClaim(PRINCIPAL_CLAIMS_KEY);
        AuthPrincipal principal = jwtPrincipalConverter.toPrincipal(principalClaims);
        verifyToken(jwtClaimsSet, principal);
        return principal;
    }

    public void verifyToken(JWTClaimsSet claims, AuthPrincipal principal) {
        JwtUtils.validateNotExpired(claims);

        try {
            Optional<AbstractUser<?>> byContactInformation = userService.findByContactInformation(principal.getName());
            VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+principal.getName()+" not found");
            AbstractUser<?> user = byContactInformation.get();
            JwtUtils.validateIssuedAfter(claims,user.getCredentialsUpdatedMillis());
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("User encoded in token not found",e);
        }
    }

    @Autowired
    public void setJwsTokenService(JwsTokenService jwsTokenService) {
        this.jwsTokenService = jwsTokenService;
    }

    @Autowired
    public void setJwtPrincipalConverter(JwtPrincipalConverter jwtPrincipalConverter) {
        this.jwtPrincipalConverter = jwtPrincipalConverter;
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }
}
