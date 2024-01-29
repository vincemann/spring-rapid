package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Owner;

import java.util.Optional;



@Component
public interface OwnerService extends CrudService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
//    Owner lazyLoadFindById(Long id);
}
