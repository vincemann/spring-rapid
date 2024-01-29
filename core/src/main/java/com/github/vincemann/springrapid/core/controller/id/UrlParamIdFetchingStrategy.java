package com.github.vincemann.springrapid.core.controller.id;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Fetches the Id from a {@link HttpServletRequest} by a url param with the key "id"
 *
 * @param <Id>
 */
public abstract class UrlParamIdFetchingStrategy<Id extends Serializable> implements IdFetchingStrategy<Id> {

    public UrlParamIdFetchingStrategy() {
    }

    @Override
    public Id fetchId(HttpServletRequest request) throws IdFetchingException {
        String id = request.getParameter("id");
        if (id == null) {
            throw new IdFetchingException("No id found in request");
        } else {
            try {
                return convert(id);
            }catch (Exception e){
                throw new IdFetchingException("Id in request cant be converted to target type",e);
            }

        }
    }

    protected abstract Id convert(String id) throws IdFetchingException;

}
