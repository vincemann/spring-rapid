package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * decorator securing owner Service
 */
@Secured
@Service
public class SecuredOwnerService
        extends AbstractSecuredUserServiceDecorator<OwnerService, Owner,Long>
        implements OwnerService{

    @Autowired
    public SecuredOwnerService(OwnerService decorated) {
        super(decorated);
    }


    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        Optional<Owner> owner = getDecorated().findByLastName(lastName);
        owner.ifPresent(o -> getAclTemplate().checkPermission(o, BasePermission.READ));
        return owner;
    }

}
