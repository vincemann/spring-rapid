package io.github.spring.lemon.auth.security;

import io.github.spring.lemon.auth.domain.AbstractUser;
import io.github.spring.lemon.auth.domain.AbstractUserRepository;
import io.github.spring.lemon.auth.domain.dto.ChangePasswordForm;
import io.github.spring.lemon.auth.domain.dto.RequestEmailChangeForm;
import io.github.spring.lemon.auth.security.domain.LemonRole;
import io.github.spring.lemon.auth.domain.dto.user.LemonUserDto;
import io.github.spring.lemon.auth.util.LecwUtils;
import io.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.core.util.RapidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;


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

//    @CalledByProxy
//    public void preAuthorizeFindByEmail(String email) throws EntityNotFoundException {
//        //only include email if user has write permission
//        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
//        EntityUtils.checkPresent(byEmail,"No User found with email: " +email);
//        getSecurityChecker().checkPermission(byEmail.get().getId(),byEmail.get().getClass(), getWritePermission());
//    }

    //this is done by mapping to specific dto
//    @CalledByProxy
//    public void postAuthorizeFindByEmail(String email, AbstractUser result){
//        //only include email if user has write permission
//        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
//        byEmail.ifPresent(new Consumer<>() {
//            @Override
//            public void accept(AbstractUser o) {
//                AbstractUser detached = JpaUtils.detach(o);
//                if(!hasWritePermission(detached)){
//                    result.setEmail(null);
//                }
//            }
//        });
//
//    }

    @CalledByProxy
    @OverrideDefaultSecurityRule
    public void preAuthorizeUpdate(AbstractUser<?> update,boolean full) throws BadEntityException, EntityNotFoundException{
        getSecurityChecker().checkPermission(update.getId(),update.getClass(), getWritePermission());
        Optional<AbstractUser> byId = userRepository.findById(update.getId());
        RapidUtils.checkPresent(byId,update.getId(),update.getClass());
        LemonUserDto currentUser = LecwUtils.currentUser();
        RapidUtils.checkNotNull(currentUser,"Authenticated user not found");
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
        getSecurityChecker().checkAuthenticated();
    }

    @CalledByProxy
    public void preAuthorizeFetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername){
        getSecurityChecker().checkAuthenticated();
    }

    @CalledByProxy
    public void preAuthorizeFetchFullToken(String authHeader){
        getSecurityChecker().checkAuthenticated();
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
