package io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import io.github.vincemann.generic.crud.lib.controller.exception.IdTransformingException;

import javax.servlet.http.HttpServletRequest;

public abstract class UrlParamIdFetchingStrategy<Id> implements IdFetchingStrategy<Id> {

    private String idUrlParamKey;

    public UrlParamIdFetchingStrategy(String idUrlParamKey) {
        this.idUrlParamKey = idUrlParamKey;
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
