package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acl.service.RapidAclServiceImpl;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.auth.service.JpaUserService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Primary
@Service
@Qualifier("owner")
public class JpaOwnerService
        extends JpaUserService<Owner,Long, OwnerRepository>
                implements OwnerService
{


    @LogInteraction
    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    public Class<?> getTargetClass(){
        return JpaOwnerService.class;
    }

}
