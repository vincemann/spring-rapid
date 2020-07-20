package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.abs.Person;
import lombok.extern.slf4j.Slf4j;

/**
 *Plugin that can be plugged in to all {@link RapidController} dealing with Person Objects
 */
@Slf4j
@ServiceComponent
public class SaveNameToWordPressDbPlugin extends ServiceExtension<SimpleCrudService<Person,Long>>
        implements GenericCrudServiceExtension<SimpleCrudService<Person,Long>,Person,Long> {


    @Override
    public Person save(Person entity) throws BadEntityException {
        if(entity!=null)
            log.debug("saving Persons name: "+ entity.getFirstName() + " into wordpress database");
        return getNext().save(entity);
    }
}
