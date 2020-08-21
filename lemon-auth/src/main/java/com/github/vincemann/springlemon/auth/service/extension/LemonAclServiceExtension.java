package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.service.SimpleLemonService;
import com.github.vincemann.springlemon.auth.util.LemonUtils;
import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclServiceExtension;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.core.service.security.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Transactional
public class LemonAclServiceExtension
        extends AbstractAclServiceExtension<SimpleLemonService>
            implements SimpleLemonServiceExtension<SimpleLemonService>
{
    private AbstractUserRepository repository;

    public LemonAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService, AbstractUserRepository repository) {
        super(permissionService, mutableAclService, mockAuthService);
        this.repository = repository;
    }


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


    public void savePostSignupAclInfo(String email){
        log.debug("saving acl info for signed up user: " + email);
        Optional<AbstractUser> byEmail = repository.findByEmail(email);
        if(!byEmail.isPresent()){
            log.warn("No user found after signup -> cant save acl permissions");
            return;
        }
        //login is needed for save full permission for authenticated
        LemonUtils.login(byEmail.get());
        savePermissionForAuthenticatedOver(byEmail.get(), BasePermission.ADMINISTRATION);
        saveFullPermissionForAdminOver(byEmail.get());
    }



}
