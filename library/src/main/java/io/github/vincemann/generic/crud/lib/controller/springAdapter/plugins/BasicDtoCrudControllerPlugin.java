package io.github.vincemann.generic.crud.lib.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Set;

public interface BasicDtoCrudControllerPlugin<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> {

    public void beforeFindEntity(Id id);

    public void afterFindEntity(ServiceE foundEntity);

    public void beforeCreateEntity(ServiceE entity);

    public void afterCreateEntity(ServiceE entity);

    public void beforeUpdateEntity(ServiceE entity);

    public void afterUpdateEntity(ServiceE entity);

    public void beforeDeleteEntity(Id id);

    public void afterDeleteEntity(Id id);

    public void beforeFindAllEntities();

    public void afterFindAllEntities(Set<? extends ServiceE> all);
}
