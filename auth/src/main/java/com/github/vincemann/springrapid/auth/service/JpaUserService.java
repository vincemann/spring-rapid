package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.*;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAnon;

/**
 * Note:
 * If you extend from this class and annotate with @{@link org.springframework.stereotype.Service} or @{@link org.springframework.stereotype.Component}
 * make sure to not also add {@link org.springframework.context.annotation.Primary}
 */
@Validated
@Slf4j
public abstract class JpaUserService
        <
                U extends AbstractUser<Id>,
                Id extends Serializable,
                R extends AbstractUserRepository<U, Id>
                >
        extends JpaCrudService<U, Id, R>
        implements UserService<U, Id>, ApplicationContextAware {


    private AuthorizationTokenService authorizationTokenService;
    private RapidSecurityContext securityContext;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidPasswordEncoder passwordEncoder;
    private AuthProperties properties;
    private PasswordValidator passwordValidator;

    public abstract U newUser();


    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<String, Object>(2);
        context.put("shared", properties.getShared());
        RapidPrincipal principal = securityContext.currentPrincipal();
        if (principal != null) {
            if (!isAnon(principal)) {
                RapidPrincipal withoutPw = new RapidPrincipal(principal);
                withoutPw.setPassword(null);
                context.put("user", withoutPw);
            }
        }

        return context;
    }

    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        Optional<U> byContactInformation = findByContactInformation(contactInformation);
        if (byContactInformation.isPresent()) {
            throw new AlreadyRegisteredException("ContactInformation: " + contactInformation + " is already taken");
        }
    }


    @Transactional
    @Override
    public U create(U user) throws BadEntityException {
        // only enforce very basic stuff
        user.setPassword(encodedPasswordIfNeeded(user.getPassword()));
        if (user.getPassword() != null)
            passwordValidator.validate(user.getPassword());
        return super.create(user);
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        super.deleteById(id);
    }



    // helper methods for special updates that enforce basic database rules but not more complex stuff like sending msges to user

    @Override
    public U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        U oldEntity = findOldEntity(userId);
        Set<String> newRoles = new HashSet<>(oldEntity.getRoles());
        newRoles.add(role);
        U update = Entity.createUpdate(oldEntity);
        update.setRoles(newRoles);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update);
    }

    @Override
    public U removeRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        U oldEntity = findOldEntity(userId);
        Set<String> newRoles = new HashSet<>(oldEntity.getRoles());
        newRoles.remove(role);
        U update = Entity.createUpdate(oldEntity);
        update.setRoles(newRoles);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update);
    }


    @Override
    public void updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        U update = Entity.createUpdate(getEntityClass(), userId);
        update.setPassword(encodedPasswordIfNeeded(password));
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        service.partialUpdate(update);
    }
    
    protected String encodedPasswordIfNeeded(String password){
        if (!passwordEncoder.isEncrypted(password)) {
            return passwordEncoder.encode(password);
        } else {
            return password;
        }
    }

    /**
     * Changes the password.
     */
    @Transactional
    public void changePassword(U user, String givenOldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.isPresent(user, "User not found");
        String oldPassword = user.getPassword();

        if (!newPassword.equals(retypeNewPassword)) {
            throw new BadEntityException("Password does not match retype password");
        }
        passwordValidator.validate(newPassword);
        // checks
        VerifyEntity.is(
                passwordEncoder.matches(givenOldPassword,
                        oldPassword), "Wrong password");

        // sets the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        log.debug("changed pw of user: " + user.getContactInformation());
        try {
            service.softUpdate(user);
        } catch (NonTransientDataAccessException e) {
            throw new RuntimeException("Could not change users password", e);
        }

    }

    @Override
    public U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        U update = Entity.createUpdate(getEntityClass(), userId);
        return updateContactInformation(update,contactInformation);
    }

    @Override
    public U updateContactInformation(U update, String contactInformation) throws EntityNotFoundException, BadEntityException {
        update.setContactInformation(contactInformation);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update);
    }

    /**
     * Fetches a new token
     *
     * @return
     */
    @Override
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException {
        Optional<U> byContactInformation = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation, "user with contactInformation: " + contactInformation + " not found");
        return authorizationTokenService.createToken(authenticatedPrincipalFactory.create(byContactInformation.get()));
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        return createNewAuthToken(securityContext.currentPrincipal().getName());
    }

    @Transactional
    //only called internally
    public U createAdmin(AuthProperties.Admin admin) {
        // create the adminUser
        U adminUser = newUser();
        adminUser.setContactInformation(admin.getContactInformation());
        adminUser.setPassword(admin.getPassword());
        adminUser.getRoles().add(AuthRoles.ADMIN);
        return adminUser;
    }


    @Autowired
    public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

    @Autowired
    public void setSecurityContext(RapidSecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Autowired
    public void setPasswordEncoder(RapidPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setPrincipalUserConverter(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }


//    protected boolean rolesUpdated(U update) throws EntityNotFoundException {
//        U old = findOldEntity(update.getId());
//        if (!old.getRoles().equals(update))
//            return true;
//        else
//            return false;
//    }

//    protected void checkForBlacklistedProperties(String... properties){
//        ArrayList<String> updatedProperties = Lists.newArrayList(properties);
//        if (updatedProperties.contains(propertyNameOf(new AbstractUser()::getRoles))
//            || updatedProperties.contains(propertyNameOf(new AbstractUser()::getContactInformation))
//            || updatedProperties.contains(propertyNameOf(new AbstractUser()::getPassword)){
//            throw new IllegalArgumentException("user specialized methods for updating fields like: roles, contactInformation or password");
//        }
//    }
//
//    protected boolean rolesPartialUpdated(U update, String... fieldsToUpdate){
//        Set<String> updatedFields = Entity.findPartialUpdatedFields(update, fieldsToUpdate);
//        if (Lists.newArrayList(updatedFields).contains()){
//            return true;
//        }else{
//            return false;
//        }
//    }


}

