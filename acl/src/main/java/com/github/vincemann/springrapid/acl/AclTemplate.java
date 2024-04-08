package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.auth.IdAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;

/**
 * API for dynamic programmatic acl permission checking.
 */
public interface AclTemplate {

    public boolean checkExpression(String securityExpression);

    /**
     * Filters a collection based on permission the authenticated User has.
     *
     */
    public <E extends IdAware<? extends Serializable>, C extends Collection<E>>
    C filter(C toFilter, Permission permission);



    /**
     * Checks if authenticated User has permission over Entity with given id & class
     * @param id
     * @param clazz
     * @param permission
     */
    public void checkPermission(Serializable id, Class<?> clazz, Permission permission) throws AccessDeniedException;

    public void checkPermission(IdAware<?> entity, Permission permission) throws AccessDeniedException;



}
