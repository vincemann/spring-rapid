package com.github.vincemann.springrapid.acl;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.service.security.SecurityChecker;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.Collection;

/**
 * API for dynamic acl permission checking.
 */
@LogInteraction
@LogException
@ServiceComponent
public interface AclSecurityChecker extends SecurityChecker, AopLoggable {

    public boolean checkExpression(String securityExpression);

    /**
     * Filters a collection based on permission the currently logged in user has.
     *
     * @param toFilter
     * @param permission
     * @param <E>
     * @param <C>
     * @return
     */
    public <E extends IdentifiableEntity<? extends Serializable>, C extends Collection<E>>
    C filter(C toFilter, String permission);



    /**
     * Checks if authenticated User has permission over Entity with given id & class
     * @param id
     * @param clazz
     * @param permission
     */
    public void checkPermission(Serializable id,Class<?> clazz,String permission) throws AccessDeniedException;


}
