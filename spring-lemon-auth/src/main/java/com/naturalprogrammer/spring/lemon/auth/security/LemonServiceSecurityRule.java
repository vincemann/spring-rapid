package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonRole;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;


@Transactional(readOnly = true)
public class LemonServiceSecurityRule extends ServiceSecurityRule {

    private AbstractUserRepository userRepository;

    @Autowired
    public LemonServiceSecurityRule(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CalledByProxy
    public void preAuthorizeSave(AbstractUser toSave){
        getSecurityChecker().checkRole(LemonRole.GOOD_ADMIN);
    }

    @CalledByProxy
    public void preAuthorizeResendVerificationMail(AbstractUser user){
        LexUtils.ensureFound(user);
        getSecurityChecker().checkPermission(user.getId(),user.getClass(), getWritePermission());
    }

    @CalledByProxy
    public void postAuthorizeFindByEmail(String email, AbstractUser result){
        //only include email if user has write permission
        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
        byEmail.ifPresent(new Consumer<>() {
            @Override
            public void accept(AbstractUser o) {
                if(!hasWritePermission(o)){
                    result.setEmail(null);
                }
            }
        });

    }

    @CalledByProxy
    public void preAuthorizeUpdateUser(AbstractUser user, AbstractUser update) throws BadEntityException, EntityNotFoundException{
        getSecurityChecker().checkPermission(user.getId(),update.getClass(), getWritePermission());
    }

    @CalledByProxy
    public void postAuthorizeProcessUser(AbstractUser user, AbstractUser result){
        //only include email if user has write permission
        if(!hasWritePermission(user)){
            result.setEmail(null);
        }
    }

    @CalledByProxy
    public void preAuthorizeForgotPassword(String email){
        //check if write permission over user
        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()){
            getSecurityChecker().checkPermission(byEmail.get().getId(),byEmail.get().getClass(), getWritePermission());
        }else {
            //let service throw more detailed exception
        }
    }


    @CalledByProxy
    public void preAuthorizeChangePassword(AbstractUser user, ChangePasswordForm changePasswordForm){
        LexUtils.ensureFound(user);
        getSecurityChecker().checkPermission(user.getId(),user.getClass(), getWritePermission());
    }

    @CalledByProxy
    public void preAuthorizeRequestEmailChange(Serializable userId, AbstractUser updatedUser, Class userClazz){
        LexUtils.ensureFound(userRepository.findById(userId));
        getSecurityChecker().checkPermission(userId,userClazz,getWritePermission());
    }

    @CalledByProxy
    public void preAuthorizeChangeEmail(Serializable userId, String changeEmailCode) {
        getSecurityChecker().checkIfAuthenticated();
    }

    @CalledByProxy
    public void preAuthorizeFetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername){
        getSecurityChecker().checkIfAuthenticated();
    }

    @CalledByProxy
    public void preAuthorizeFetchFullToken(String authHeader){
        getSecurityChecker().checkIfAuthenticated();
    }



    private boolean hasWritePermission(AbstractUser user){
        try {
            getSecurityChecker().checkPermission(user.getId(),user.getClass(), getWritePermission());
            return true;
        }catch (AccessDeniedException e){
            return false;
        }
    }
}
