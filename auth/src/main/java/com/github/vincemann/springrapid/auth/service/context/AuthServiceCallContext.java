package com.github.vincemann.springrapid.auth.service.context;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ThrowingSupplier;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

public class AuthServiceCallContext extends ServiceCallContext {

    private UserUtils userUtils;
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    // dont use, just use userUtils.findAuthenticated which calls service.findByContactInformation which will be cached via aop
//    public <U extends AbstractUser> U resolveAuthenticated(){
//        return getCached("authenticated",() -> userUtils.findAuthenticatedUser());
//    }

    public <U extends AbstractUser> U resolveUser(Serializable id) throws EntityNotFoundException {
        U user = getThrowingCached(computeUserKey(id), new ThrowingSupplier<U, EntityNotFoundException>() {
            @Override
            public U get() throws EntityNotFoundException {
                return userUtils.findUserById(id);
            }
        });
        addCached(computeUserKey(user.getContactInformation()),user);
        return user;
    }

    public <U extends AbstractUser> U resolveUserByContactInformation(String contactInformation) throws EntityNotFoundException {
        U user = getThrowingCached(computeUserKey(contactInformation), new ThrowingSupplier<U, EntityNotFoundException>() {
            @Override
            public U get() throws EntityNotFoundException {
                Optional<U> byContactInformation = userService.findByContactInformation(contactInformation);
                VerifyEntity.isPresent(byContactInformation, "User with contact information: " + contactInformation + " not found");
                return byContactInformation.get();
            }
        });
        addCachedEntity(computeUserKey(user.getId()),user);
        return user;
    }

    public <U extends AbstractUser> Optional<U> resolveOptionalUserByContactInformation(String contactInformation) {
        Optional<U> user = getCached(computeUserKey(contactInformation), new Supplier<Optional<U>>() {
            @Override
            public Optional<U> get() {
                return userService.findByContactInformation(contactInformation);
            }
        });
        if (user.isPresent())
            addCachedEntity(computeUserKey(user.get().getId()),user);
        return user;
    }

    private String computeUserKey(Serializable id){
        return computeKey(userService.getEntityClass(),id);
    }
}
