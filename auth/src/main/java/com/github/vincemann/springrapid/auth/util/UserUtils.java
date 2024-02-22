package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.Optional;

public class UserUtils {

    private UserService userService;

    private IdConverter idConverter;


    public AbstractUser extractUserFromClaims(JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        // fetch the user
        Optional<AbstractUser> byId = userService.findById(id);
        VerifyEntity.isPresent(byId, "User with id: " + id + " not found");
        return byId.get();
    }

    public <T extends AbstractUser> T findAuthenticatedUser(){
        if (!RapidSecurityContext.isAuthenticated()){
            throw new AccessDeniedException("No user logged in");
        }
        Optional<T> userByContactInformation = (Optional<T>) userService.findByContactInformation(RapidSecurityContext.getName());
        try {
            VerifyEntity.isPresent(userByContactInformation,"user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found",e);
        }
        return userByContactInformation.get();
    }




    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
