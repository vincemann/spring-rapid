package vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides an interface for 'how to get the Ip from HttpSevletRequest'
 */
public interface IdFetchingStrategy<Id> {
    public Id fetchId(HttpServletRequest httpServletRequest) throws IdFetchingException;
}
