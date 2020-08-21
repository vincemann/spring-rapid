package com.github.vincemann.springrapid.core.service.security;

import org.springframework.security.access.AccessDeniedException;

public interface SecurityChecker {

    /**
     * Checks whether currently logged in user is authenticated.
     */
    public void checkAuthenticated() throws AccessDeniedException;

    /**
     * Checks whether currently logged in user is authenticated.
     */
    public boolean isAuthenticated();

    /**
     * Check if authenticated user has @role
     * @param role
     */
    public void checkRole(String role) throws AccessDeniedException;

    public boolean hasRole(String role);
}
