package com.github.vincemann.springrapid.auth.service.context;

import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;

public class AuthServiceCallContextHolder {

    public static AuthServiceCallContext getContext(){
        return ServiceCallContextHolder.getContext();
    }
}
