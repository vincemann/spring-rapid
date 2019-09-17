package io.github.vincemann.generic.crud.lib.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public abstract class AbstractDtoCrudControllerSpringAdapterPlugin<ServiceE extends IdentifiableEntity<Id>,Dto extends IdentifiableEntity<Id>, Id extends Serializable> implements BasicDtoCrudControllerPlugin<ServiceE,Id> {

    private DtoCrudControllerSpringAdapter controller;

    public void beforeCreate(Dto dto, HttpServletRequest httpServletRequest){}
    public void beforeUpdate(Dto dto, HttpServletRequest httpServletRequest){}
    public void beforeDelete(Id id, HttpServletRequest httpServletRequest){}
    public void beforeFind(Id id, HttpServletRequest httpServletRequest){}
    public void beforeFindAll(HttpServletRequest httpServletRequest){}


    @Override
    public void beforeFindEntity(Id id) {

    }

    @Override
    public void afterFindEntity(ServiceE foundEntity) {

    }

    @Override
    public void beforeCreateEntity(ServiceE entity) {

    }

    @Override
    public void afterCreateEntity(ServiceE entity) {

    }

    @Override
    public void beforeUpdateEntity(ServiceE entity) {

    }

    @Override
    public void afterUpdateEntity(ServiceE entity) {

    }

    @Override
    public void beforeDeleteEntity(Id id) {

    }

    @Override
    public void afterDeleteEntity(Id id) {

    }

    @Override
    public void beforeFindAllEntities() {

    }

    @Override
    public void afterFindAllEntities(Set<? extends ServiceE> all) {

    }
}
