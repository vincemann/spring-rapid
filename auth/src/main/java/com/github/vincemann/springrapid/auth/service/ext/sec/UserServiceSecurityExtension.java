package com.github.vincemann.springrapid.auth.service.ext.sec;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.ext.UserServiceExtension;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
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

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAdmin;


@Transactional
@Slf4j
public class UserServiceSecurityExtension
        extends SecurityExtension<UserService>
            implements UserServiceExtension<UserService> {


    private UserService userService;
    private JweTokenService jweTokenService;

    private RapidSecurityContext securityContext;


    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getNext().create(entity);
    }

    @Override
    public AbstractUser addRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().addRole(userId,role);
    }

    @Override
    public AbstractUser removeRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().removeRole(userId,role);
    }

    @Override
    public void updatePassword(Serializable userId, String password) throws EntityNotFoundException, BadEntityException {
        getNext().updatePassword(userId,password);
    }

    @Override
    public AbstractUser updateContactInformation(Serializable userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().updateContactInformation(userId,contactInformation);
    }

    @Override
    public AbstractUser updateContactInformation(AbstractUser update, String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().updateContactInformation(update,contactInformation);
    }

    @Override
    public AbstractUser createUser() {
        return getNext().createUser();
    }

    @LogInteraction
    @Override
    public AbstractUser fullUpdate(AbstractUser entity) throws BadEntityException, EntityNotFoundException {
        checkUpdatePermissions(entity);
        return getNext().fullUpdate(entity);
    }

    @Override
    public AbstractUser softUpdate(AbstractUser entity) throws EntityNotFoundException, BadEntityException {
        checkUpdatePermissions(entity);
        return getNext().softUpdate(entity);
    }

    @LogInteraction
    @Override
    public AbstractUser partialUpdate(AbstractUser entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        checkUpdatePermissions(entity);
        return getNext().partialUpdate(entity, fieldsToUpdate);
    }


    protected void checkUpdatePermissions(AbstractUser update) throws EntityNotFoundException {
        getSecurityChecker().checkPermission(update, BasePermission.WRITE);
        Optional<AbstractUser<Serializable>> oldUserOp = userService.findById(update.getId());
        VerifyEntity.isPresent(oldUserOp, update.getId(), update.getClass());
        AbstractUser oldUser = oldUserOp.get();
        AuthorizationTemplate.assertAuthenticated();
        RapidPrincipal currPrincipal = securityContext.currentPrincipal();
        checkRoleChangingPermissions(oldUser, update, currPrincipal);
    }

    /**
     * Check current Users role and decide what role adjustments he can make.
     * user cant update roles
     *
     */
    protected void checkRoleChangingPermissions(AbstractUser<?> old, AbstractUser<?> newUser, RapidPrincipal currentUser) {
        // admin tries to edit
        if (isAdmin(currentUser) &&
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
    public void changePassword(AbstractUser user, String oldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().changePassword(user, oldPassword, newPassword, retypeNewPassword);
    }

    @LogInteraction
    @Override
    public void requestContactInformationChange(AbstractUser user, String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException, BadEntityException {
        VerifyEntity.isPresent(user,"User who's contactInformation should get changed does not exist");
        getSecurityChecker().checkPermission(user, BasePermission.WRITE);
        getNext().requestContactInformationChange(user, newContactInformation);
    }


    // admin can just change contactInformation via normal update
    @LogInteraction
    @Override
    public AbstractUser changeContactInformation(String changeContactInformationCode) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
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
        RapidPrincipal authenticated = securityContext.currentPrincipal();
        VerifyAccess.condition(authenticated.getName().equals(contactInformation) ||
                isAdmin(authenticated), Message.get("com.github.vincemann.notGoodAdminOrSameUser"));
        return getNext().createNewAuthToken(contactInformation);
    }


    @Autowired
    public void setSecurityContext(RapidSecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }
}
