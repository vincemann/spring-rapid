package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.proxy.GenericSimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.abs.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 *Extension that can be plugged in to all {@link RapidController} dealing with Person Objects
 */
@Slf4j
@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveNameToWordPressDbExtension extends ServiceExtension<SimpleCrudService<Person,Long>>
        implements GenericSimpleCrudServiceExtension<SimpleCrudService<Person,Long>,Person,Long> {


    @Override
    public Person save(Person entity) throws BadEntityException {
        if(entity!=null)
            log.debug("saving Persons name: "+ entity.getFirstName() + " into wordpress database");
        return getNext().save(entity);
    }
}
