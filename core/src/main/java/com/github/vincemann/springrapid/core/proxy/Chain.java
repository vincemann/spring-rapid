package com.github.vincemann.springrapid.core.proxy;

interface Chain{

    /**
     * Last element in chain is for all extensions within the same chain the same, that's why no extension as arg is needed.
     * Last element is always the proxied service. (Could be a proxy still, if the proxy factory was given a proxy as the service to proxy)
     */
    Object getLast();


    Object getNext(BasicServiceExtension<?> extension);

}
