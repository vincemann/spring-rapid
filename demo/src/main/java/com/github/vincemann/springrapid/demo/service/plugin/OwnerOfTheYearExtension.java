package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.core.proxy.GenericSimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;
import com.github.vincemann.springrapid.demo.service.OwnerService;
import com.github.vincemann.springrapid.demo.service.jpa.OwnerJPAService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Optional;

@ServiceComponent
//leave the scope as singleton in this case, bc it is hard to verify interactions with prototype scopes in test
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerOfTheYearExtension
        extends ServiceExtension<OwnerService>
            implements OwnerService, GenericSimpleCrudServiceExtension<OwnerService,Owner,Long> {


    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getNext().findOwnerOfTheYear();
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getNext().findByLastName(lastName);
    }


    @Override
    public OwnerRepository getRepository() {
        return getNext().getRepository();
    }

    public Class<?> getTargetClass(){
        return OwnerJPAService.class;
    }

}
