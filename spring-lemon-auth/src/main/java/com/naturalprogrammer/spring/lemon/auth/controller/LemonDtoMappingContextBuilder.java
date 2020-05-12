package com.naturalprogrammer.spring.lemon.auth.controller;

import com.google.common.collect.Sets;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoMappingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.naturalprogrammer.spring.lemon.auth.controller.LemonDtoEndpoint.*;

@Getter
@Setter
@NoArgsConstructor
public class LemonDtoMappingContextBuilder extends DtoMappingContextBuilder {

    public static LemonDtoMappingContextBuilder builder(){
        return new LemonDtoMappingContextBuilder();
    }

    public static LemonDtoMappingContextBuilder builder(RapidDtoMappingContext dtoMappingContext){
        return new LemonDtoMappingContextBuilder(dtoMappingContext);
    }

    public LemonDtoMappingContextBuilder(RapidDtoMappingContext mc) {
        super(mc);
    }

    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.addAll(Sets.newHashSet(SIGN_UP,RESET_PASSWORD,FETCH_BY_EMAIL,CHANGE_EMAIL, VERIFY_USER));
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(FETCH_BY_EMAIL);
        return findEndpoints;
    }
}
