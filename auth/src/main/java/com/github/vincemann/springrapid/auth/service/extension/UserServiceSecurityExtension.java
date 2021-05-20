package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.service.extensions.security.AbstractSecurityExtension;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.security.RapidAuthSecurityContextChecker;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContextChecker;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;


@Transactional
@Slf4j
public class UserServiceSecurityExtension
        extends AbstractSecurityExtension<UserService>
            implements UserServiceExtension<UserService> {


    private UserService userService;
    private RapidAuthSecurityContextChecker securityContextChecker;

    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        securityContextChecker.checkAdmin();
        return getNext().save(entity);
    }

    @LogInteraction
    @Override
    public void resendVerificationMail(AbstractUser user) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().resendVerificationMail(user);
    }


    @LogInteraction
    @Override
    public AbstractUser update(AbstractUser update, Boolean full) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(update, BasePermission.WRITE);
        Optional<AbstractUser<Serializable>> oldUserOp = userService.findById(update.getId());
        VerifyEntity.isPresent(oldUserOp, update.getId(), update.getClass());
        AbstractUser oldUser = oldUserOp.get();
        RapidSecurityContextChecker.checkAuthenticated();
        RapidAuthAuthenticatedPrincipal currPrincipal = securityContextChecker.getSecurityContext().currentPrincipal();
        checkRoleChangingPermissions(oldUser, update, currPrincipal);
//        getProxyController().overrideDefaultExtension();
        return getLast().update(update, full);
    }

    /**
     * Check current Users role and decide what role adjustments he can make.
     */
    protected void checkRoleChangingPermissions(AbstractUser<?> old, AbstractUser<?> newUser, RapidAuthAuthenticatedPrincipal currentUser) {
        // admin tries to edit
        if (currentUser.isAdmin() &&
                !currentUser.getId().equals(old.getId().toString())) {
            return;
        } else {
            //no update of roles possible
            if (!old.getRoles().equals(newUser.getRoles())) {
                throw new AccessDeniedException("Only Admin can update Roles");
            }
//            newUser.setRoles(old.getRoles());
        }
    }

    @LogInteraction
    @Override
    public void forgotPassword(String email) throws EntityNotFoundException {
        //check if write permission over user
        Optional<AbstractUser> byEmail = userService.findByEmail(email);
        VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");

        // anon has to be able to reset password without being logged in
//        AbstractUser user = byEmail.get();
//        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), BasePermission.WRITE);
        getNext().forgotPassword(email);
    }

    @LogInteraction
    @Override
    public void changePassword(AbstractUser user, ChangePasswordDto changePasswordForm) throws EntityNotFoundException, BadEntityException {
//        LexUtils.ensureFound(user);
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().changePassword(user, changePasswordForm);
    }

    @LogInteraction
    @Override
    public void requestEmailChange(AbstractUser user, RequestEmailChangeDto emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        VerifyEntity.isPresent(user,"User who's email should get changed does not exist");
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().requestEmailChange(user, emailChangeForm);
    }


    // admin can just change email via normal update
//    @LogInteraction
//    @Override
//    public AbstractUser changeEmail(String changeEmailCode) throws EntityNotFoundException,  BadEntityException {
//        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
//        return getNext().changeEmail(changeEmailCode);
//    }

    @LogInteraction
    @Override
    public String createNewAuthToken(String email) throws EntityNotFoundException {
        RapidAuthAuthenticatedPrincipal authenticated = securityContextChecker.getSecurityContext().currentPrincipal();
        VerifyAccess.condition(authenticated.getEmail().equals(email) ||
                authenticated.isAdmin(), Message.get("com.naturalprogrammer.spring.notGoodAdminOrSameUser"));
        return getNext().createNewAuthToken(email);
    }



    @Autowired
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void injectSecurityContextChecker(RapidAuthSecurityContextChecker securityContextChecker) {
        this.securityContextChecker = securityContextChecker;
    }

    //todo did not find method... problems?
    ////@LogInteraction(level = LogInteraction.Level.TRACE)
//    @CalledByProxy
//    public void postAuthorizeProcessUser(AbstractUser user, AbstractUser result){
//        //only include email if user has write permission
//        if(!hasWritePermission(user)){
//            result.setEmail(null);
//        }
//    }

    //    @Override
//    public Map<String, String> fetchFullToken(String authHeader) {
//        getSecurityChecker().checkAuthenticated();
//        return getNext().fetchFullToken(authHeader);
//    }


//    private boolean hasWritePermission(AbstractUser user){
//        try {
//            getSecurityChecker().checkPermission(user.getId(),user.getClass(), BasePermission.WRITE);
//            return true;
//        }catch (AccessDeniedException e){
//            return false;
//        }
//    }

    //    @CalledByProxy
//    public void preAuthorizeFindByEmail(String email) throws EntityNotFoundException {
//        //only include email if user has write permission
//        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
//        EntityUtils.checkPresent(byEmail,"No User found with email: " +email);
//        getSecurityChecker().checkPermission(byEmail.get().getId(),byEmail.get().getClass(), BasePermission.WRITE);
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
}
