package com.github.vincemann.springrapid.syncdemo.service.ext;

import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.abs.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Example Demo Extension that can be plugged in to all {@link CrudService}s dealing with Person Entities
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveNameToWordPressDbExtension extends ServiceExtension<CrudService<Person,Long>>
        implements GenericCrudServiceExtension<CrudService<Person,Long>,Person,Long> {


    @Override
    public Person create(Person entity) throws BadEntityException {
        if(entity!=null)
            log.debug("saving Persons name: "+ entity.getFirstName() + " into wordpress database");
        return getNext().create(entity);
    }
}
