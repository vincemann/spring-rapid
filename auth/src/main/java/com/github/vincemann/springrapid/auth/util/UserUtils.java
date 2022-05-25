package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.Optional;

public class UserUtils {

//    @Autowired
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //    public static void setUserService(UserService<AbstractUser<Serializable>, Serializable> userService) {
//        UserUtils.userService = userService;
//    }


    public <T extends AbstractUser> T findAuthenticatedUser(){
        if (!RapidSecurityContext.isAuthenticated()){
            throw new AccessDeniedException("No user logged in");
        }
        Optional<T> userByEmail = (Optional<T>) userService.findByEmail(RapidSecurityContext.getName());
        try {
            VerifyEntity.isPresent(userByEmail,"user with email: " + RapidSecurityContext.getName()+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with email: " + RapidSecurityContext.getName()+ " could not be found",e);
        }
        return userByEmail.get();
    }

    public <T extends AbstractUser> T findUserById(Serializable id) throws EntityNotFoundException, BadEntityException {
        Optional<T> userByEmail = (Optional<T>) userService.findById(id);
        VerifyEntity.isPresent(userByEmail,"user with email: " + RapidSecurityContext.getName()+ " could not be found");
        return userByEmail.get();
    }
}
