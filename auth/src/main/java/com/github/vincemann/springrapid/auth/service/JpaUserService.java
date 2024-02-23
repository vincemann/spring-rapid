package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAnon;
import static com.github.vincemann.springrapid.core.util.MethodNameUtil.propertyNameOf;

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
        implements UserService<U, Id>,
        ApplicationContextAware
{

    private RapidPasswordEncoder passwordEncoder;
    private PasswordValidator passwordValidator;


    @Transactional
    //only called internally
    public U createAdmin(AuthProperties.Admin admin) {
        // create the adminUser
        U adminUser = createUser();
        adminUser.setContactInformation(admin.getContactInformation());
        adminUser.setPassword(admin.getPassword());
        adminUser.getRoles().add(AuthRoles.ADMIN);
        return adminUser;
    }




    @Override
    public U createUser() {
        return BeanUtils.instantiateClass(getEntityClass());
    }

    @Transactional
    @Override
    public U create(U user) throws BadEntityException {
        // only enforce very basic stuff
        user.setPassword(encodedPasswordIfNeeded(user.getPassword()));
        if (user.getPassword() != null && !passwordEncoder.isEncoded(user.getPassword()))
            passwordValidator.validate(user.getPassword());
        return super.create(user);
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
    public U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        U update = Entity.createUpdate(getEntityClass(), userId);
        update.setPassword(encodedPasswordIfNeeded(password));
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update);
    }
    
    protected String encodedPasswordIfNeeded(String password){
        if (!passwordEncoder.isEncoded(password)) {
            return passwordEncoder.encode(password);
        } else {
            return password;
        }
    }

    @Override
    public U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        Optional<U> user = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,contactInformation,getEntityClass());
        if (user.get().hasRole(AuthRoles.BLOCKED)){
            throw new BadEntityException("user is already blocked");
        }
        return addRole(user.get().getId(),AuthRoles.BLOCKED);
    }

    @Override
    public U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        U update = Entity.createUpdate(getEntityClass(), userId);
        update.setContactInformation(contactInformation);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update);
    }


    @Override
    public Optional<U> findByContactInformation(String contactInformation) {
        return getRepository().findByContactInformation(contactInformation);
    }

    @Autowired
    public void setPasswordEncoder(RapidPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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

