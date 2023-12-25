package com.github.vincemann.springrapid.core.advice;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.stereotype.Service;


public class PointCuts {

    public void checkProxyIsExtension() {
        Object unproxied = ProxyUtils.getTargetClass(this);
        if (unproxied instanceof AbstractServiceExtension) {
            // Logic when ProxyUtils.unproxy(this) is an instance of AbstractServiceExtension
        } else {
            // Logic when ProxyUtils.unproxy(this) is not an instance of AbstractServiceExtension
        }
    }

        // Other methods...
}
