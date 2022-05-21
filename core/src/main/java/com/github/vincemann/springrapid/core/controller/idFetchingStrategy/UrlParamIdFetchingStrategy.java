package com.github.vincemann.springrapid.core.controller.idFetchingStrategy;

import javax.servlet.http.HttpServletRequest;

/**
 * Fetches the Id from a {@link HttpServletRequest} by a url param with the key "id"
 *
 * @param <Id>
 */
public abstract class UrlParamIdFetchingStrategy implements IdFetchingStrategy {

    public UrlParamIdFetchingStrategy() {
    }

    @Override
    public <Id> Id fetchId(HttpServletRequest request) throws IdFetchingException {
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

    protected abstract <Id> Id convert(String id);

}
