package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.AbstractDtoMappingContextBuilder;
import com.google.common.collect.Sets;

import java.util.List;

//lemon uses full urls in properties, thus i can get the full urls from there
public class UserDtoMappingContextBuilder extends AbstractDtoMappingContextBuilder<AbstractUserController,UserDtoMappingContextBuilder> {


    public UserDtoMappingContextBuilder(AbstractUserController controller) {
        super(controller);
    }

    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.addAll(Sets.newHashSet(
                getController().getLemonProperties().getController().getSignupUrl(),
                getController().getLemonProperties().getController().getResetPasswordUrl(),
                getController().getLemonProperties().getController().getFetchByEmailUrl(),
                getController().getLemonProperties().getController().getChangeEmailUrl(),
                getController().getLemonProperties().getController().getVerifyUserUrl()));
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(getController().getLemonProperties().getController().getFetchByEmailUrl());
        return findEndpoints;
    }
    
}
