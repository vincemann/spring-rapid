package com.github.vincemann.springrapid.core.controller.id;

/**
 * The fetched Id is of Type {@link Long}.
 *
 * @see UrlParamIdFetchingStrategy
 */
public class LongUrlParamIdFetchingStrategy extends UrlParamIdFetchingStrategy<Long> {
    public LongUrlParamIdFetchingStrategy() {
        super();
    }

    @Override
    protected Long convert(String id) throws IdFetchingException {
        try {
            return Long.parseLong(id);
        }catch (NumberFormatException e){
            throw new IdFetchingException(e);
        }
    }
}
