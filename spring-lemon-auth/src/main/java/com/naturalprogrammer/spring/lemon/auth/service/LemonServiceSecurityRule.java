package com.naturalprogrammer.spring.lemon.auth.service;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.sun.xml.bind.v2.model.core.ID;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Optional;


public class LemonServiceSecurityRule extends ServiceSecurityRule {

    private AbstractUserRepository userRepository;

    @Autowired
    public LemonServiceSecurityRule(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CalledByProxy
    public void preAuthorizeResendVerificationMail(AbstractUser user){
        getSecurityChecker().checkPermission(user.getId(),user.getClass(), BasePermission.WRITE.getPattern());
    }

    @CalledByProxy
    public void postAuthorizeFindByEmail(AbstractUser user, AbstractUser result){
        //only include email if user has write permission
        if(!hasWritePermission(user)){
            result.setEmail(null);
        }
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
            getSecurityChecker().checkPermission(byEmail.get().getId(),byEmail.get().getClass(), BasePermission.WRITE.getPattern());
        }else {
            //let service throw more detailed exception
        }
    }


    @CalledByProxy
    public void preAuthorizeChangePassword(AbstractUser user, ChangePasswordForm changePasswordForm){
        getSecurityChecker().checkPermission(user.getId(),user.getClass(), BasePermission.WRITE.getPattern());
    }

    @CalledByProxy
    public void preAuthorizeRequestEmailChange(AbstractUser user, AbstractUser updatedUser){
        getSecurityChecker().checkPermission(user.getId(),user.getClass(), BasePermission.WRITE.getPattern());
    }

    @CalledByProxy
    public void preAuthorizeChangeEmail(ID userId, String changeEmailCode){
        getSecurityChecker().checkExpression("isAuthenticated()");
    }

    @CalledByProxy
    public void preAuthorizeFetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername){
        getSecurityChecker().checkExpression("isAuthenticated()");
    }

    @CalledByProxy
    public void preAuthorizeFetchFullToken(String authHeader){
        getSecurityChecker().checkExpression("isAuthenticated()");
    }



    private boolean hasWritePermission(AbstractUser user){
        try {
            getSecurityChecker().checkPermission(user.getId(),user.getClass(), BasePermission.WRITE.getPattern());
            return true;
        }catch (AccessDeniedException e){
            return false;
        }
    }
}
