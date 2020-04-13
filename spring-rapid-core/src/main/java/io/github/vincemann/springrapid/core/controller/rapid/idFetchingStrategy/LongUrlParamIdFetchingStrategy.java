package io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy;

import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdTransformingException;
import org.springframework.lang.Nullable;

/**
 * The fetched Id is of Type {@link Long}.
 * @see UrlParamIdFetchingStrategy
 */
public class LongUrlParamIdFetchingStrategy extends UrlParamIdFetchingStrategy<Long> {
    public LongUrlParamIdFetchingStrategy(@Nullable String idUrlParamKey) {
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
