package com.naturalprogrammer.spring.lemon.auth.controller;

import com.google.common.collect.Sets;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.naturalprogrammer.spring.lemon.auth.controller.LemonDtoEndpoint.*;

@Getter
@Setter
@NoArgsConstructor
public class LemonDtoMappingContextBuilder extends DtoMappingContextBuilder {
    @Override
    protected List<String> getAllEndpoints() {
        super.getAllEndpoints().addAll(Sets.newHashSet(SIGN_UP,RESET_PASSWORD,FETCH_BY_EMAIL,CHANGE_EMAIL));
        return super.getAllEndpoints();
    }

    @Override
    protected List<String> getFindEndpoints() {
        super.getFindEndpoints().add(FETCH_BY_EMAIL);
        return super.getFindEndpoints();
    }
}
