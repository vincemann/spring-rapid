package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.ChainController;

public interface SecurityChainController extends ChainController {

    public void overrideDefaultExtension();
}
