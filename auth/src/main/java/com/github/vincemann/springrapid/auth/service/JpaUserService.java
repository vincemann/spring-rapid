package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.MethodNameUtil.propertyName;

/**
 * Note:
 * If you extend from this class and annotate with @{@link org.springframework.stereotype.Service} or @{@link org.springframework.stereotype.Component}
 * make sure to not also add {@link org.springframework.context.annotation.Primary}
 */
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
    private RapidAclService aclService;


    //only called internally
    public U createAdmin(AuthProperties.Admin admin) {
        Assert.notNull(admin);
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
        Assert.notNull(user);
        // only enforce very basic stuff
        if (user.getPassword() != null && !passwordEncoder.isEncoded(user.getPassword()))
            passwordValidator.validate(user.getPassword());
        user.setPassword(encodePasswordIfNeeded(user.getPassword()));
        U saved = super.create(user);
        saveAclInfo(saved);
        return saved;
    }

    protected void saveAclInfo(U saved){
        aclService.savePermissionForUserOverEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
    }

    @Transactional(readOnly = true)
    @Override
    public U findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        Assert.notNull(contactInformation);
        Optional<U> user = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,contactInformation,getEntityClass());
        return user.get();
    }


    // helper methods for special updates that enforce basic database rules but not more complex stuff like sending msges to user

    @Transactional
    @Override
    public U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(role);
        Assert.notNull(userId);

        U oldEntity = findOldEntity(userId);
        Set<String> newRoles = new HashSet<>(oldEntity.getRoles());
        newRoles.add(role);
        U update = Entity.createUpdate(oldEntity);
        update.setRoles(newRoles);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update, propertyName(update::getRoles), propertyName(update::getCredentialsUpdatedMillis));
    }


    @Transactional
    @Override
    public U removeRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(role);
        Assert.notNull(userId);

        U oldEntity = findOldEntity(userId);
        Set<String> newRoles = new HashSet<>(oldEntity.getRoles());
        boolean removed = newRoles.remove(role);
        Assert.isTrue(removed,"user did not contain role: " + role);
        U update = Entity.createUpdate(oldEntity);
        update.setRoles(newRoles);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update,propertyName(update::getRoles), propertyName(update::getCredentialsUpdatedMillis));
    }


    @Transactional
    @Override
    public U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(password);
        Assert.notNull(userId);

        if (!passwordEncoder.isEncoded(password))
            passwordValidator.validate(password);
        U update = Entity.createUpdate(getEntityClass(), userId);
        update.setPassword(encodePasswordIfNeeded(password));
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update,propertyName(update::getPassword), propertyName(update::getCredentialsUpdatedMillis));
    }
    
    protected String encodePasswordIfNeeded(String password){
        if (!passwordEncoder.isEncoded(password)) {
            return passwordEncoder.encode(password);
        } else {
            return password;
        }
    }

    @Transactional
    @Override
    public U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"contact-information");

        Optional<U> user = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,contactInformation,getEntityClass());
        if (user.get().hasRole(AuthRoles.BLOCKED)){
            throw new BadEntityException("user is already blocked");
        }
        return addRole(user.get().getId(),AuthRoles.BLOCKED);
    }

    @Transactional
    @Override
    public U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(userId);
        Assert.notNull(contactInformation);

        U update = Entity.createUpdate(getEntityClass(), userId);
        update.setContactInformation(contactInformation);
        update.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return service.partialUpdate(update,propertyName(update::getContactInformation), propertyName(update::getCredentialsUpdatedMillis));
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<U> findByContactInformation(String contactInformation) {
        Assert.notNull(contactInformation);
        return getRepository().findByContactInformation(contactInformation);
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        aclService.deleteAclOfEntity(getEntityClass(),id,false);
        super.deleteById(id);
    }

    @Autowired
    public void setPasswordEncoder(RapidPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Autowired
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
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

