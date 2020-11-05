package com.github.vincemann.springrapid.acl.service.extensions;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Getter
@Slf4j
/**
 * Removes Acl's on delete, if existing.
 */
@Transactional
@LogInteraction(Severity.TRACE)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CleanUpAclServiceExtension
        extends AbstractAclServiceExtension<CrudService>
                implements CrudServiceExtension<CrudService>
{
    @Setter
    private boolean deleteCascade = true;


    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
        deleteAcl(id,getEntityClass());
    }

//    @CalledByProxy
//    public void onAfterDeleteById(Serializable id,Class entityClass) throws EntityNotFoundException, BadEntityException {
//
//    }

    private void deleteAcl(Serializable id, Class entityClass){
        log.debug("deleting acl for entity with id: " + id + " and class: " + entityClass);
        //delete acl as well
        ObjectIdentity oi = new ObjectIdentityImpl(entityClass, id);
        log.debug("ObjectIdentity getting deleted: " + oi);
        //todo delete children ist nur richtig wenn ich wirklich one to n habe mit Delete Cascade!
        getMutableAclService().deleteAcl(oi,deleteCascade);
        log.debug("Acl successfully deleted");
    }

}
