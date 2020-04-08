package io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy;

import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides an interface for 'how to get the Id from an HttpServletRequest'
 *
 */
public interface IdFetchingStrategy<Id> {
    public Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
