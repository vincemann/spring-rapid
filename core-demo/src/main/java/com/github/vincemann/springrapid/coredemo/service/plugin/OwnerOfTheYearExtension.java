package com.github.vincemann.springrapid.coredemo.service.plugin;

import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;

import java.util.Optional;

@ServiceComponent
//leave the scope as singleton in this case, bc it is hard to verify interactions with prototype scopes in test
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerOfTheYearExtension
        extends BasicServiceExtension<OwnerService>
            implements OwnerService, GenericCrudServiceExtension<OwnerService,Owner,Long> {


    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getNext().findOwnerOfTheYear();
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getNext().findByLastName(lastName);
    }

//    @Override
//    public Class<?> getTargetClass(){
//        return JpaOwnerService.class;
//    }

}
