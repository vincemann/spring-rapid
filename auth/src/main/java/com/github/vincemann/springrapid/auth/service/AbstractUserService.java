package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;


public abstract class AbstractUserService
        <
                U extends AbstractUser<Id>,
                Id extends Serializable,
                R extends AbstractUserRepository<U, Id>
                >
        implements UserService<U, Id>
{

    private RapidPasswordEncoder passwordEncoder;
    private PasswordValidator passwordValidator;
    private RapidAclService aclService;
    private R repository;

    private Class<U> entityClass;

    public AbstractUserService() {
        this.entityClass = (Class<U>) GenericTypeResolver.resolveTypeArguments(this.getClass(),AbstractUserService.class)[0];
    }

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
        return BeanUtils.instantiateClass(entityClass);
    }

    @Transactional
    @Override
    public U create(U user) throws BadEntityException {
        Assert.notNull(user,"user to create must not be null");
        VerifyEntity.notNull(user.getPassword(),"password");
        VerifyEntity.notNull(user.getContactInformation(),"contactInformation");
        VerifyEntity.notEmpty(user.getRoles(),"roles");
        // only enforce very basic stuff
        if (user.getPassword() != null && !passwordEncoder.isEncoded(user.getPassword()))
            passwordValidator.validate(user.getPassword());
        user.setPassword(encodePasswordIfNeeded(user.getPassword()));
        U saved = repository.save(user);
        saveAclInfo(saved);
        return saved;
    }

    protected void saveAclInfo(U saved){
        aclService.grantUserPermissionForEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
    }

    @Transactional(readOnly = true)
    @Override
    public U findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        Assert.notNull(contactInformation);
        Optional<U> user = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,contactInformation, entityClass);
        return user.get();
    }


    // helper methods for special updates that enforce basic database rules but not more complex stuff like sending msges to user

    @Transactional
    @Override
    public U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(role,"role to add must not be null");
        Assert.notNull(userId,"user id must not be null");

        U user = RepositoryUtil.findPresentById(repository,userId);
        user.getRoles().add(role);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return user;
    }


    @Transactional
    @Override
    public U removeRole(Id userId, String role) throws EntityNotFoundException {
        Assert.notNull(role,"role to remove must not be null");
        Assert.notNull(userId,"user id must not be null");

        U user = RepositoryUtil.findPresentById(repository,userId);
        boolean removed = user.getRoles().remove(role);
        Assert.isTrue(removed,"user did not contain role to remove: " + role);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return user;
    }


    @Transactional
    @Override
    public U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        Assert.notNull(password);
        Assert.notNull(userId);

        if (!passwordEncoder.isEncoded(password))
            passwordValidator.validate(password);
        U user = RepositoryUtil.findPresentById(repository,userId);
        user.setPassword(encodePasswordIfNeeded(password));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return user;
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

        U user = findPresentByContactInformation(contactInformation);
        if (user.hasRole(AuthRoles.BLOCKED)){
            throw new BadEntityException("user is already blocked");
        }
        return addRole(user.getId(),AuthRoles.BLOCKED);
    }

    @Transactional
    @Override
    public U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException {
        Assert.notNull(userId);
        Assert.notNull(contactInformation);

        U user = RepositoryUtil.findPresentById(repository, userId);
        user.setContactInformation(contactInformation);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        return user;
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<U> findByContactInformation(String contactInformation) {
        Assert.notNull(contactInformation);
        return getRepository().findByContactInformation(contactInformation);
    }

    @Transactional
    public void delete(Id id) throws EntityNotFoundException {
        aclService.deleteAclOfEntity(getEntityClass(),id,false);
        repository.deleteById(id);
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

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }

    public Class<U> getEntityClass() {
        return entityClass;
    }

    protected RapidPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    protected PasswordValidator getPasswordValidator() {
        return passwordValidator;
    }

    protected RapidAclService getAclService() {
        return aclService;
    }


}

