package io.github.vincemann.demo.service.plugins;

import io.github.vincemann.demo.model.abs.Person;
import io.github.vincemann.generic.crud.lib.service.decorator.implementations.PluginCrudServiceDecorator;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *Plugin that can be plugged in to all {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter} dealing with Person Objects
 */
@Slf4j
@Component
public class PersonNameSavingPlugin extends PluginCrudServiceDecorator.Plugin<Person,Long> {

    @Override
    public void onBeforeSave(Person entity) throws BadEntityException {
        super.onBeforeSave(entity);
        log.debug("saving Persons name: "+ entity.getFirstName() + " into separate person database table");
        /* save name of every created Person in seperate db table for example*/
    }
}
