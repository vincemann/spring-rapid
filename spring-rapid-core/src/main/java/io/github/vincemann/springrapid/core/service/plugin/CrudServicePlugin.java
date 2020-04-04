package io.github.vincemann.springrapid.core.service.plugin;

import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ServiceComponent
public abstract class CrudServicePlugin<E extends IdentifiableEntity<Id>, Id extends Serializable> {

//    public void onBeforeSave(E toSave, Class<? extends E> entityClass){}
//    public void onBeforeUpdate(E toUpdate, boolean full, Class<? extends E> entityClass){}
//    public void onBeforeDeleteById(Id id, Class<? extends E> entityClass){}
//    public void onBeforeFindById(Id id,  Class<? extends E> entityClass){}
//    public void onBeforeFindAll(Class<? extends E> entityClass){}
//
//    public E onAfterSave(E saved, Class<? extends E> entityClass){return saved;}
//    public E onAfterUpdate(E updated, boolean full, Class<? extends E> entityClass){return updated;}
//    public void onAfterDeleteById(Id id, Class<? extends E> entityClass){}
//    public void onAfterFindById(Id id,  Class<? extends E> entityClass){}
//    public Set<E> onAfterFindAll(Set<E> found, Class<? extends E> entityClass){return found;}
}
