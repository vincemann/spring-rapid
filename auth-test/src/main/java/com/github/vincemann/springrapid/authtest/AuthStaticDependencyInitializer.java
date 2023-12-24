package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.service.context.AuthServiceCallContext;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.coretest.CoreStaticDependencyInitializer;
import com.github.vincemann.springrapid.coretest.StaticDependencyInitializer;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthStaticDependencyInitializer extends CoreStaticDependencyInitializer {

    private UserUtils userUtils;


    @Autowired(required = false)
    public void setUserUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Override
    public void initializeStaticDependencies() {
        super.initializeStaticDependencies();
        if (userUtils != null)
            AuthServiceCallContext.setUserUtils(userUtils);
    }
}
