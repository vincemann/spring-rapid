package com.github.vincemann.springrapid.acl;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;

/**
 * API for dynamic acl permission checking.
 */
@ServiceComponent
@LogInteraction
public interface AclSecurityChecker extends AopLoggable {

    public boolean checkExpression(String securityExpression);

    /**
     * Filters a collection based on permission the authenticated User has.
     *
     * @param toFilter
     * @param permission
     * @param <E>
     * @param <C>
     * @return
     */
    public <E extends IdentifiableEntity<? extends Serializable>, C extends Collection<E>>
    C filter(C toFilter, Permission permission);



    /**
     * Checks if authenticated User has permission over Entity with given id & class
     * @param id
     * @param clazz
     * @param permission
     */
    public void checkPermission(Serializable id, Class<?> clazz, Permission permission) throws AccessDeniedException;

    public default void checkPermission(IdentifiableEntity<?> entity, Permission permission) throws AccessDeniedException{
        checkPermission(entity.getId(),entity.getClass(),permission);
    }



}
