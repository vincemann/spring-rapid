package com.github.vincemann.springlemon.auth.controller;

import com.google.common.collect.Sets;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.github.vincemann.springlemon.auth.controller.LemonDtoEndpoint.*;

@Getter
@Setter
@NoArgsConstructor
public class LemonDtoMappingContextBuilder extends DtoMappingContextBuilder {

    public static LemonDtoMappingContextBuilder builder(){
        return new LemonDtoMappingContextBuilder();
    }

    public static LemonDtoMappingContextBuilder builder(DtoMappingContext dtoMappingContext){
        return new LemonDtoMappingContextBuilder(dtoMappingContext);
    }

    public LemonDtoMappingContextBuilder(DtoMappingContext mc) {
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
