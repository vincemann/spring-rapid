package io.github.vincemann.springrapid.demo.service.plugin;

import io.github.vincemann.springrapid.demo.model.abs.Person;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;

/**
 *Plugin that can be plugged in to all {@link SpringAdapterJsonDtoCrudController} dealing with Person Objects
 */
@Slf4j
@ServiceComponent
public class SaveNameToWordPressDbPlugin extends CrudServicePlugin<Person,Long> {

    public void onBeforeSave(Person toSave, Class<? extends Person> entityClass) {
        if(toSave!=null)
            log.debug("saving Persons name: "+ toSave.getFirstName() + " into wordpress database");
    }
}
