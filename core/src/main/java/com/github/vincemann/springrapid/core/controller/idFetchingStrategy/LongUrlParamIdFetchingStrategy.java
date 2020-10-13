package com.github.vincemann.springrapid.core.controller.idFetchingStrategy;

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
    protected Long convert(String id) {
        return Long.parseLong(id);
    }
}
