package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractDelegatingUserService implements UserService<User,Long>
{
    private OwnerService ownerService;
    private VetService vetService;

    @Override
    public User createAdmin(AuthProperties.Admin admin) {
        return (User) findService().createAdmin(admin);
    }

    @Override
    public User createUser() {
        return (User) findService().createUser();
    }

    @Override
    public User create(User user) throws BadEntityException {
        return (User) findService().create(user);
    }


    @Override
    public User partialUpdate(User update, String... fieldsToUpdate) throws EntityNotFoundException {
        return (User) findService().partialUpdate(update,fieldsToUpdate);
    }

    @Override
    public User fullUpdate(User update) throws EntityNotFoundException {
        return (User) findService().fullUpdate(update);
    }

    @Override
    public User softUpdate(User entity) throws EntityNotFoundException {
        return (User) findService().softUpdate(entity);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public Optional<User> findById(Long id) {
        return findService().findById(id);
    }

    @Override
    public User findPresentById(Long id) throws EntityNotFoundException {
        return (User) findService().findPresentById(id);
    }

    @Override
    public Set<User> findSome(Set<Long> ids) {
        return findService().findSome(ids);
    }

    @Override
    public Set<User> findAll() {
        return findService().findAll();
    }

    @Override
    public Set<User> findAll(List<QueryFilter<? super User>> jpqlFilters, List<EntityFilter<? super User>> entityFilters, List<SortingExtension> sortingStrategies) {
        return findService().findAll(jpqlFilters,entityFilters,sortingStrategies);
    }

    @Override
    public User findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        return (User) findService().findPresentByContactInformation(contactInformation);
    }

    @Override
    public User addRole(Long userId, String role) throws EntityNotFoundException, BadEntityException {
        return (User) findService().addRole(userId, role);
    }

    @Override
    public User removeRole(Long userId, String role) throws EntityNotFoundException, BadEntityException {
        return (User) findService().removeRole(userId, role);
    }

    @Override
    public User updatePassword(Long userId, String password) throws EntityNotFoundException, BadEntityException {
        return (User) findService().updatePassword(userId, password);
    }


    @Override
    public User blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        return (User) findService().blockUser(contactInformation);
    }

    @Override
    public User updateContactInformation(Long userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        return (User) findService().updateContactInformation(userId, contactInformation);
    }

    @Override
    public Optional<User> findByContactInformation(String contactInformation) {
        return (Optional<User>) findService().findByContactInformation(contactInformation);
    }

    @Override
    public void deleteById(Long aLong) throws EntityNotFoundException {
        findService().deleteById(aLong);
    }

    private UserService findService(){
        List<String> roles = RapidSecurityContext.getRoles();
        Assert.isTrue(!roles.isEmpty(), "must be authenticated");
        boolean isVet = roles.contains(MyRoles.VET);
        boolean isOwner = roles.contains(MyRoles.OWNER);
        Assert.isTrue(!(isVet && isOwner));
        if (isVet)
            return vetService;
        else if(isOwner)
            return ownerService;
        else
            throw new IllegalArgumentException("unexpected role");
    }


    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
    }


    public void setVetService(VetService vetService) {
        this.vetService = vetService;
    }
}
