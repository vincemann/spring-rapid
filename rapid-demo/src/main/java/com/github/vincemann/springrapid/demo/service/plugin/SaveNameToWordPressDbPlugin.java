package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.demo.model.abs.Person;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.controller.rapid.RapidController;
import com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;

/**
 *Plugin that can be plugged in to all {@link RapidController} dealing with Person Objects
 */
@Slf4j
@ServiceComponent
public class SaveNameToWordPressDbPlugin extends CrudServicePlugin<Person,Long> {

    public void onBeforeSave(Person toSave, Class<? extends Person> entityClass) {
        if(toSave!=null)
            log.debug("saving Persons name: "+ toSave.getFirstName() + " into wordpress database");
    }
}
