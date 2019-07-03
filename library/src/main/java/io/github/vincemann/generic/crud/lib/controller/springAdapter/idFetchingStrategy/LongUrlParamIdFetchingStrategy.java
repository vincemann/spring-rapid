package io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy;

import io.github.vincemann.generic.crud.lib.controller.exception.IdTransformingException;

/**
 * The fetched Id is of Type Long.
 * {@see UrlParamIdFetchingStrategy}
 */
public class LongUrlParamIdFetchingStrategy extends UrlParamIdFetchingStrategy<Long> {
    public LongUrlParamIdFetchingStrategy(String idUrlParamKey) {
        super(idUrlParamKey);
    }

    @Override
    protected Long transformToIdType(String id) throws IdTransformingException {
        try {
            return Long.parseLong(id);
        }catch (NumberFormatException e){
            throw new IdTransformingException(e);
        }
    }
}
