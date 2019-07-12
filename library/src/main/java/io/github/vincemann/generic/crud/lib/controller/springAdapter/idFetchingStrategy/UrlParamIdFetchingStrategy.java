package io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * Fetches the Id from a {@link HttpServletRequest} by a url param with the name given by
 * {@link UrlParamIdFetchingStrategy#idUrlParamKey}
 * @param <Id>
 */
public abstract class UrlParamIdFetchingStrategy<Id> implements IdFetchingStrategy<Id> {
    private static final String DEFAULT_ID_URL_PARAM_KEY = "id";

    private String idUrlParamKey;

    public UrlParamIdFetchingStrategy(@Nullable String idUrlParamKey) {
        if(idUrlParamKey==null){
            this.idUrlParamKey=DEFAULT_ID_URL_PARAM_KEY;
        }else {
            this.idUrlParamKey = idUrlParamKey;
        }
    }

    @Override
    public Id fetchId(HttpServletRequest request) throws IdFetchingException {
        String id=  request.getParameter(idUrlParamKey);
        if(id==null){
            throw new IdFetchingException("No value for idUrlParamKey: " + idUrlParamKey);
        }else {
            try {
                return transformToIdType(id);
            }catch (IdTransformingException e){
                throw new IdFetchingException(e);
            }
        }
    }

    protected abstract Id transformToIdType(String id) throws IdTransformingException;

    public String getIdUrlParamKey() {
        return idUrlParamKey;
    }
}
