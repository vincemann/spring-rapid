package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.service.extensions.security.AbstractSecurityExtension;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.security.RapidAuthSecurityContextChecker;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContextChecker;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
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
    private JweTokenService jweTokenService;

    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        securityContextChecker.checkAdmin();
        return getNext().save(entity);
    }

    @Override
    public AbstractUser signupAdmin(AbstractUser admin) throws AlreadyRegisteredException, BadEntityException {
        securityContextChecker.checkAdmin();
        return getNext().signupAdmin(admin);
    }

    @LogInteraction
    @Override
    public AbstractUser fullUpdate(AbstractUser entity) throws BadEntityException, EntityNotFoundException {
        checkUpdatePermissions(entity);
        // todo why getLast
        return getLast().fullUpdate(entity);
    }

    @Override
    public AbstractUser softUpdate(AbstractUser entity) throws EntityNotFoundException, BadEntityException {
        checkUpdatePermissions(entity);
        // todo why getLast
        return getLast().softUpdate(entity);
    }

    // everybody must be able to do this
//    @LogInteraction
//    @Override
//    public void resendVerificationMail(AbstractUser user) throws EntityNotFoundException, BadEntityException {
//        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
//        getNext().resendVerificationMail(user);
//    }

    @LogInteraction
    @Override
    public AbstractUser partialUpdate(AbstractUser entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        checkUpdatePermissions(entity);
        // todo why getLast
        return getLast().partialUpdate(entity,fieldsToRemove);
    }


    protected void checkUpdatePermissions(AbstractUser update) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(update, BasePermission.WRITE);
        Optional<AbstractUser<Serializable>> oldUserOp = userService.findById(update.getId());
        VerifyEntity.isPresent(oldUserOp, update.getId(), update.getClass());
        AbstractUser oldUser = oldUserOp.get();
        RapidSecurityContextChecker.checkAuthenticated();
        RapidAuthAuthenticatedPrincipal currPrincipal = securityContextChecker.getSecurityContext().currentPrincipal();
        checkRoleChangingPermissions(oldUser, update, currPrincipal);
//        getProxyController().overrideDefaultExtension();
//        return getLast().update(update, full,fieldsToRemove);
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
            if (newUser.getRoles() != null){
                if (!old.getRoles().equals(newUser.getRoles())) {
                    //no update of roles possible for non admin users
                    throw new AccessDeniedException("Only Admin can update Roles");
                }
            }
//            newUser.setRoles(old.getRoles());
        }
    }

    @LogInteraction
    @Override
    public void forgotPassword(String contactInformation) throws EntityNotFoundException {
        //check if write permission over user
        Optional<AbstractUser> byContactInformation = userService.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+contactInformation+" not found");

        // anon has to be able to reset password without being logged in
//        AbstractUser user = byContactInformation.get();
//        getSecurityChecker().checkPermission(user.getId(), getLast().getEntityClass(), BasePermission.WRITE);
        getNext().forgotPassword(contactInformation);
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
    public void requestPrincipalChange(AbstractUser user, RequestContactInformationChangeDto contactInformationChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        VerifyEntity.isPresent(user,"User who's contactInformation should get changed does not exist");
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().requestPrincipalChange(user, contactInformationChangeForm);
    }


    // if you have the code you can change the contactInformation
    // admin can just change contactInformation via normal update
    @LogInteraction
    @Override
    public AbstractUser changeContactInformation(String changeContactInformationCode) throws EntityNotFoundException,  BadEntityException {
        try {
            JWTClaimsSet claims = jweTokenService.parseToken(changeContactInformationCode);
            Serializable userId = claims.getSubject();
            if (userId==null){
                throw new BadEntityException("No user found with id: " + userId);
            }
            getSecurityChecker().checkPermission(userId,getLast().getEntityClass(), BasePermission.WRITE);
        } catch (BadTokenException e) {
            throw new BadEntityException(e);
        }
        return getNext().changeContactInformation(changeContactInformationCode);
    }

    @LogInteraction
    @Override
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException {
        RapidAuthAuthenticatedPrincipal authenticated = securityContextChecker.getSecurityContext().currentPrincipal();
        VerifyAccess.condition(authenticated.getContactInformation().equals(contactInformation) ||
                authenticated.isAdmin(), Message.get("com.github.vincemann.notGoodAdminOrSameUser"));
        return getNext().createNewAuthToken(contactInformation);
    }



    @Autowired
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void injectSecurityContextChecker(RapidAuthSecurityContextChecker securityContextChecker) {
        this.securityContextChecker = securityContextChecker;
    }

    @Autowired
    public void injectJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    //todo did not find method... problems?
    ////@LogInteraction(level = LogInteraction.Level.TRACE)
//    @CalledByProxy
//    public void postAuthorizeProcessUser(AbstractUser user, AbstractUser result){
//        //only include contactInformation if user has write permission
//        if(!hasWritePermission(user)){
//            result.setContactInformation(null);
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
//    public void preAuthorizeFindByContactInformation(String contactInformation) throws EntityNotFoundException {
//        //only include contactInformation if user has write permission
//        Optional<AbstractUser> byContactInformation = userRepository.findByContactInformation(contactInformation);
//        EntityUtils.checkPresent(byContactInformation,"No User found with contactInformation: " +contactInformation);
//        getSecurityChecker().checkPermission(byContactInformation.get().getId(),byContactInformation.get().getClass(), BasePermission.WRITE);
//    }

    //this is done by mapping to specific dto
//    @CalledByProxy
//    public void postAuthorizeFindByContactInformation(String contactInformation, AbstractUser result){
//        //only include contactInformation if user has write permission
//        Optional<AbstractUser> byContactInformation = userRepository.findByContactInformation(contactInformation);
//        byContactInformation.ifPresent(new Consumer<>() {
//            @Override
//            public void accept(AbstractUser o) {
//                AbstractUser detached = JpaUtils.detach(o);
//                if(!hasWritePermission(detached)){
//                    result.setContactInformation(null);
//                }
//            }
//        });
//
//    }
}
