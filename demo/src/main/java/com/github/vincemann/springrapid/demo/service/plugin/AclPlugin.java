package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.nickvl.xspring.core.log.aop.annotation.LogDebug;
import com.github.vincemann.springrapid.core.advice.log.AopLoggable;
import com.github.vincemann.springrapid.core.advice.log.LogInteraction;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServiceComponent
public class AclPlugin extends CrudServicePlugin<IdentifiableEntity<Long>,Long>  {

    public void onBeforeSave(IdentifiableEntity<Long> toSave, Class<? extends IdentifiableEntity<Long>> entityClass) {
        log.debug("creating acl list for Entity with class: " + entityClass);
    }

    public void onBeforeDeleteById(Long id, Class<? extends IdentifiableEntity<Long>> entityClass) {
        log.debug("deleting acl list for Entity with class: " + entityClass);
    }
}
