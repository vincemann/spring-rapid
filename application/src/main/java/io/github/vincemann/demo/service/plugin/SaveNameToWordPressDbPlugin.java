package io.github.vincemann.demo.service.plugin;

import io.github.vincemann.demo.model.abs.Person;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *Plugin that can be plugged in to all {@link SpringAdapterJsonDtoCrudController} dealing with Person Objects
 */
@Slf4j
@ServiceComponent
public class SaveNameToWordPressDbPlugin extends CrudServicePlugin<Person,Long> {

    public void onBeforeSave(Person entity){
        log.debug("saving Persons name: "+ entity.getFirstName() + " into wordpress database");
        /* save name of every created Person in seperate db table for example*/
    }
}
