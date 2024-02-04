package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Owner;

import java.util.Optional;



public interface OwnerService extends CrudService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
//    Owner lazyLoadFindById(Long id);
}
