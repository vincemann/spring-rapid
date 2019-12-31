package io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.exception.IdFetchingException;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides an interface for 'how to get the Id from an HttpServletRequest'
 *
 */
public interface IdFetchingStrategy<Id> {
    public Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
