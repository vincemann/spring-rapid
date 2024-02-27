package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

public class SecuredUserService
        // looks ugly but is just UserService in raw form
        extends AbstractSecuredUserServiceDecorator<UserService<AbstractUser<Serializable>,Serializable>, AbstractUser<Serializable>, Serializable>
        implements UserService<AbstractUser<Serializable>,Serializable>
{


    public SecuredUserService(UserService<AbstractUser<Serializable>, Serializable> decorated) {
        super(decorated);
    }


}
