package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

public class AclUserService <
        U extends AbstractUser<Id>,
        Id extends Serializable,
        R extends AbstractUserRepository<U, Id>
        > extends AbstractUserService<U,Id,R> {

    private RapidAclService aclService;

    @Transactional
    @Override
    public U create(U user) throws BadEntityException, InsufficientPasswordStrengthException {
        U saved = super.create(user);
        saveAclInfo(saved);
        return saved;
    }

    protected void saveAclInfo(U saved){
        aclService.grantUserPermissionForEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
    }

    @Transactional
    @Override
    public void delete(Id id) throws EntityNotFoundException {
        super.delete(id);
        aclService.deleteAclOfEntity(getEntityClass(),id,false);
    }

    @Autowired
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
    }
}
