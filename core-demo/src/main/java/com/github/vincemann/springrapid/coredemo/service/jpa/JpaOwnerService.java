package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Qualifier("noProxy")
@Service
@ServiceComponent
public class JpaOwnerService
        extends JPACrudService<Owner,Long, OwnerRepository>
                implements OwnerService, AopLoggable, TargetClassAware {


    @LogInteraction
    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    public Class<?> getTargetClass(){
        return JpaOwnerService.class;
    }


    /**
     * Owner named "42" is owner of the year
     * @return
     */
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals("42");
        }).findFirst();
    }
}
