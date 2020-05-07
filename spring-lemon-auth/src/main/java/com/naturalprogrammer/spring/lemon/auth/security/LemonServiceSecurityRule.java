package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.RequestEmailChangeForm;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonRole;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.util.LecwUtils;
import io.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.core.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;


@Transactional
@Slf4j
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
    @OverrideDefaultSecurityRule
    public void preAuthorizeUpdate(AbstractUser<?> update,boolean full) throws BadEntityException, EntityNotFoundException{
        getSecurityChecker().checkPermission(update.getId(),update.getClass(), getWritePermission());
        Optional<AbstractUser> byId = userRepository.findById(update.getId());
        EntityUtils.checkPresent(byId,update.getId(),update.getClass());
        LemonUserDto currentUser = LecwUtils.currentUser();
        EntityUtils.checkNotNull(currentUser,"Authenticated user not found");
        adjustRoles(byId.get(),update,currentUser);
    }

    /**
     * Check current Users role and decide what role adjustments he can make.
     */
    protected void adjustRoles(AbstractUser<?> old, AbstractUser<?> newUser, LemonUserDto currentUser) {
        // Good admin tries to edit
        if (currentUser.isGoodAdmin() &&
                !currentUser.getId().equals(old.getId().toString())) {
            return;
        }else {
            //no update of roles possible
            newUser.setRoles(old.getRoles());
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
    public void preAuthorizeRequestEmailChange(Serializable userId, RequestEmailChangeForm emailChangeForm, Class userClazz){
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
