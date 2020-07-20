package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;
import com.github.vincemann.springrapid.demo.service.OwnerService;

import java.util.Optional;

@ServiceComponent
public class OwnerOfTheYearPlugin extends ServiceExtension<OwnerService> implements OwnerService, GenericCrudServiceExtension<OwnerService,Owner,Long> {


    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        Optional<Owner> ownerOfTheYear = getNext().findOwnerOfTheYear();
        System.out.println("onAfterFindOwnerOfTheYear Hookmethod called");
        System.out.println("Owner of the year : " + ownerOfTheYear);
        return ownerOfTheYear;
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getNext().findByLastName(lastName);
    }


    @Override
    public OwnerRepository getRepository() {
        return getNext().getRepository();
    }
}
