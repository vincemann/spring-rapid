package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.security.LemonSecurityContextChecker;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.auth.util.LemonValidationUtils;
import com.github.vincemann.springrapid.acl.proxy.SecurityServiceExtension;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContextChecker;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;


@Transactional
@Slf4j
public class LemonServiceSecurityExtension
        extends SecurityServiceExtension<SimpleUserService>
            implements SimpleUserServiceExtension<SimpleUserService> {


    private UserService<AbstractUser<Serializable>, Serializable, ?> unsecuredUserService;
    private LemonSecurityContextChecker securityContextChecker;

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        securityContextChecker.checkGoodAdmin();
        return getNext().save(entity);
    }

    @Override
    public void resendVerificationMail(AbstractUser user) throws EntityNotFoundException {
        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), getWritePermission());
        getNext().resendVerificationMail(user);
    }


    @Override
    public AbstractUser update(AbstractUser update, Boolean full) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(update.getId(), getLast().getEntityClass(), getWritePermission());
        Optional<AbstractUser<Serializable>> oldUserOp = unsecuredUserService.findById(update.getId());
        AbstractUser oldUser = VerifyEntity.isPresent(oldUserOp, update.getId(), update.getClass());
        RapidSecurityContextChecker.checkAuthenticated();
        LemonAuthenticatedPrincipal currPrincipal = securityContextChecker.getSecurityContext().currentPrincipal();
        checkRoleChangingPermissions(oldUser, update, currPrincipal);
        getProxyController().overrideDefaultExtension();
        return getNext().update(update, full);
    }

    /**
     * Check current Users role and decide what role adjustments he can make.
     */
    protected void checkRoleChangingPermissions(AbstractUser<?> old, AbstractUser<?> newUser, LemonAuthenticatedPrincipal currentUser) {
        // Good admin tries to edit
        if (currentUser.isGoodAdmin() &&
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

    @Override
    public void forgotPassword(String email) throws EntityNotFoundException {
        //check if write permission over user
        AbstractUser byEmail = unsecuredUserService.findByEmail(email);
        getSecurityChecker().checkPermission(byEmail.getId(), getLast().getEntityClass(), getWritePermission());
        getNext().forgotPassword(email);
    }


    @Override
    public void changePassword(AbstractUser user, ChangePasswordForm changePasswordForm) throws EntityNotFoundException {
//        LexUtils.ensureFound(user);
        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), getWritePermission());
        getNext().changePassword(user, changePasswordForm);
    }


    @Override
    public void requestEmailChange(AbstractUser user, RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
        VerifyEntity.isPresent(user,"User who's email should get changed does not exist");
        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), getWritePermission());
        getNext().requestEmailChange(user, emailChangeForm);
    }


    @Override
    public AbstractUser changeEmail(AbstractUser user, String changeEmailCode) throws EntityNotFoundException, BadTokenException {
        VerifyEntity.isPresent(user,"User who's email should get changed does not exist");
        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), getWritePermission());
        return getNext().changeEmail(user, changeEmailCode);
    }


    @Override
    public String fetchNewAuthToken(String email) {
        LemonAuthenticatedPrincipal authenticated = securityContextChecker.getSecurityContext().currentPrincipal();
        LemonValidationUtils.ensureAuthority(authenticated.getEmail().equals(email) ||
                authenticated.isGoodAdmin(), "com.naturalprogrammer.spring.notGoodAdminOrSameUser");
        return getNext().fetchNewAuthToken(email);
    }


    @Unsecured
    @Autowired
    public void injectUnsecuredUserService(UserService<AbstractUser<Serializable>, Serializable, ?> unsecuredUserService) {
        this.unsecuredUserService = unsecuredUserService;
    }

    @Autowired
    public void injectSecurityContextChecker(LemonSecurityContextChecker securityContextChecker) {
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
//            getSecurityChecker().checkPermission(user.getId(),user.getClass(), getWritePermission());
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
}
