package com.github.vincemann.springrapid.demo.service.jpa;

import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;
import com.github.vincemann.springrapid.demo.service.OwnerService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import com.github.vincemann.aoplog.annotation.AopLoggable;

import com.github.vincemann.aoplog.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Qualifier("noProxy")
@Service
@ServiceComponent
public class OwnerJPAService extends JPACrudService<Owner,Long, OwnerRepository> implements OwnerService, AopLoggable {


    @Log
    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    @Override
    protected Class<Owner> provideEntityClass() {
        return Owner.class;
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
