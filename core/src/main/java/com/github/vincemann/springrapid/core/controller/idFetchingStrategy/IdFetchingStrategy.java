package com.github.vincemann.springrapid.core.controller.idFetchingStrategy;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * API for fetching the id from an {@link HttpServletRequest}.
 *
 */
public interface IdFetchingStrategy<Id extends Serializable> {
    public Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
