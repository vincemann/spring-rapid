package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

public abstract class UserUtils {

    private UserUtils(){}


    public static <T extends AbstractUser> T findAuthenticatedUser(UserService userService){
        VerifyAccess.condition(RapidSecurityContext.isAuthenticated(),"No user logged in");
        Optional<T> userByContactInformation = (Optional<T>) userService.findByContactInformation(RapidSecurityContext.getName());
        try {
            VerifyEntity.isPresent(userByContactInformation,"user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found",e);
        }
        return userByContactInformation.get();
    }


}
