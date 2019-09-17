package io.github.vincemann.demo.plugins;

import io.github.vincemann.demo.dtos.PersonDto;
import io.github.vincemann.demo.model.abs.Person;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.plugins.AbstractDtoCrudControllerSpringAdapterPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 *Extension that can be plugged in to all {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter} dealing with Person Objects
 */
@Slf4j
public class PersonNameSavingPlugin extends AbstractDtoCrudControllerSpringAdapterPlugin<Person, PersonDto,Long> {


    @Override
    public void beforeCreateEntity(Person entity) {
        log.debug("saving Persons name: "+ entity.getFirstName() + " into person database table");
        /* save name of every created Person in seperate db table for example*/
        super.beforeCreateEntity(entity);
    }
}
