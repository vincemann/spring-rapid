package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;

import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclServiceExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
public class LemonAclServiceExtension
        extends AbstractAclServiceExtension<SimpleUserService>
            implements SimpleUserServiceExtension<SimpleUserService>
{
    private SimpleUserService<AbstractUser<?>,?> unsecuredUserService;


    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        AbstractUser saved = (AbstractUser) getNext().save(entity);
        savePostSignupAclInfo(saved.getEmail());
        return saved;
    }


    @Override
    public AbstractUser signup(AbstractUser user) throws BadEntityException {
        AbstractUser saved = getNext().signup(user);
        savePostSignupAclInfo(user.getEmail());
        return saved;
    }

    @Override
    public void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
        savePostSignupAclInfo(admin.getEmail());
    }


    public void savePostSignupAclInfo(String emailOfSignedUp){
        log.debug("saving acl info for signed up user: " + emailOfSignedUp);
        AbstractUser user = null;
        try {
            user = unsecuredUserService.findByEmail(emailOfSignedUp);
        } catch (EntityNotFoundException e) {
            log.warn("No user found after signup -> cant save acl permissions");
            return;
        }
        savePermissionForUserOver(emailOfSignedUp,user, BasePermission.ADMINISTRATION);
        saveFullPermissionForAdminOver(user);
    }


    @Autowired
    public void injectUnsecuredUserService(SimpleUserService unsecuredUserService) {
        this.unsecuredUserService = unsecuredUserService;
    }
}
