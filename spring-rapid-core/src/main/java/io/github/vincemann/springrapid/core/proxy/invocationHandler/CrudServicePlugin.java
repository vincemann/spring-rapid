package io.github.vincemann.springrapid.core.proxy.invocationHandler;

import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Plugin accepted by {@link io.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy}.
 * Define hook methods as follows:
 *
 * void onBeforeServiceMethod(all,args,of,service,method)
 * void onBeforeServiceMethod(all,args,of,service,method,Class entityClass)  see: {@link CrudService#getEntityClass()}
 * void onAfterServiceMethod(all,args,of,service,method)
 * void onAfterServiceMethod(all,args,of,service,method, Class entityClass)
 * T onAfterServiceMethod(all,args,of,service,method)   -> the return value will update the services return value.
 * T onAfterServiceMethod(all,args,of,service,method, Class entityClass)
 *
 */
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
