package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDtoMappingContextBuilder extends DtoMappingContextBuilder {

    private LemonProperties lemonProperties;


    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.addAll(Sets.newHashSet(
                lemonProperties.controller.endpoints.signup,
                lemonProperties.controller.endpoints.resetPassword,
                lemonProperties.controller.endpoints.fetchByEmail,
                lemonProperties.controller.endpoints.changeEmail,
                lemonProperties.controller.endpoints.verifyUser));
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(lemonProperties.controller.endpoints.fetchByEmail);
        return findEndpoints;
    }

    @Autowired
    public void injectLemonProperties(LemonProperties lemonProperties) {
        this.lemonProperties = lemonProperties;
    }
}
