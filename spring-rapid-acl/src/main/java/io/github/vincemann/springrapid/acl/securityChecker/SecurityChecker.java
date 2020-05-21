package io.github.vincemann.springrapid.acl.securityChecker;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * API for dynamic acl permission checking.
 */
@ServiceComponent
public interface SecurityChecker {

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
     * Checks whether currently logged in user is authenticated.
     */
    public void checkAuthenticated() throws AccessDeniedException;;

    /**
     * Checks if authenticated User has permission over Entity with given id & class
     * @param id
     * @param clazz
     * @param permission
     */
    public void checkPermission(Serializable id,Class<?> clazz,String permission) throws AccessDeniedException;

    /**
     * Check if authenticated user has @role
     * @param role
     */
    public void checkRole(String role) throws AccessDeniedException;;
}
