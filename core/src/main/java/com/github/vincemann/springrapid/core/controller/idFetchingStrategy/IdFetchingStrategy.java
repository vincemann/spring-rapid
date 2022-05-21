package com.github.vincemann.springrapid.core.controller.idFetchingStrategy;

import javax.servlet.http.HttpServletRequest;

/**
 * API for fetching the id from an {@link HttpServletRequest}.
 *
 */
public interface IdFetchingStrategy {
    public <Id> Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
