package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;

public interface ChainController {
//    void dontCallTargetMethod();

    /**
     * If {@link ServiceExtension} calls this, then result can safely be casted to <T>.
     * @return
     */
    CrudService getLast();
    <T> T getNext(AbstractServiceExtension<T,?> extension);

}
