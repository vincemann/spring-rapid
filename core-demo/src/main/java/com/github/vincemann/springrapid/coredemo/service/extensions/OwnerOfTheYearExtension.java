package com.github.vincemann.springrapid.coredemo.service.extensions;

import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * Example Demo Extension that can be plugged in to all {@link CrudService}s dealing with Owner Entities
 */
@Slf4j
@ServiceComponent
//leaving the scope as singleton in this case, bc it is hard to verify interactions with prototype scopes in test
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerOfTheYearExtension
        extends BasicServiceExtension<OwnerService>
            implements OwnerService, GenericCrudServiceExtension<OwnerService,Owner,Long> {



    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        log.debug("Owner of the year was queried");
        // i.E. count all queries for owner of the year and store in db ...
        return getNext().findOwnerOfTheYear();
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getNext().findByLastName(lastName);
    }

    @Override
    public Owner lazyLoadFind(Long id) {
        return getNext().lazyLoadFind(id);
    }
}
