package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

public class UserUtils {

//    @Autowired
    private UserService userService;

    @Lazy
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
        Optional<T> userByContactInformation = (Optional<T>) userService.findByContactInformation(RapidSecurityContext.getName());
        try {
            VerifyEntity.isPresent(userByContactInformation,"user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found",e);
        }
        return userByContactInformation.get();
    }


    public <T extends AbstractUser> T findUserById(Serializable id) throws EntityNotFoundException {
        Optional<T> userByContactInformation = (Optional<T>) userService.findById(id);
        VerifyEntity.isPresent(userByContactInformation,"user with contactInformation: " + RapidSecurityContext.getName()+ " could not be found");
        return userByContactInformation.get();
    }
}
