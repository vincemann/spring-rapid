package com.github.vincemann.springrapid.core.controller.idFetchingStrategy;

import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.exception.IdFetchingException;

import javax.servlet.http.HttpServletRequest;

/**
 * API for fetching the id from an {@link HttpServletRequest}.
 *
 */
public interface IdFetchingStrategy<Id> {
    public Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
