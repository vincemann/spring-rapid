package com.github.vincemann.springrapid.core.security;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import org.springframework.security.access.AccessDeniedException;


@LogInteraction
@LogException
@LogConfig(ignoreSetters = true,ignoreGetters = true,logAllChildrenMethods = true)
public interface SecurityChecker extends AopLoggable {

    /**
     * Checks whether currently logged in user is authenticated.
     */
    public void checkAuthenticated() throws AccessDeniedException;

    /**
     * Check if authenticated user has @role
     * @param role
     */
    public void checkRole(String role) throws AccessDeniedException;

}
