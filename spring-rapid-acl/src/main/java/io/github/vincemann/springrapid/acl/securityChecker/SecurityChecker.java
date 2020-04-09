package io.github.vincemann.springrapid.acl.securityChecker;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Dynamic acl permission checking.
 */
@ServiceComponent
public interface SecurityChecker {
    public boolean checkExpression(String securityExpression);

    public <E extends IdentifiableEntity<? extends Serializable>, C extends Collection<E>>
    C filter(C toFilter, String permission);

    public void checkIfAuthenticated();

    /**
     * Checks if authenticated User has @permission over Entity with given @id,@class
     * @param id
     * @param clazz
     * @param permission
     */
    public void checkPermission(Serializable id,Class<?> clazz,String permission);

    /**
     * Check if authenticated user has @role
     * @param role
     */
    public void checkRole(String role);
}
