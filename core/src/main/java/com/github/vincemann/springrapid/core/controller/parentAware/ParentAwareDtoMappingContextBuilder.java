package com.github.vincemann.springrapid.core.controller.parentAware;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParentAwareDtoMappingContextBuilder extends DtoMappingContextBuilder {

    public static ParentAwareDtoMappingContextBuilder builder(){
        return new ParentAwareDtoMappingContextBuilder();
    }

    public static ParentAwareDtoMappingContextBuilder builder(DtoMappingContext mc){
        return new ParentAwareDtoMappingContextBuilder(mc);
    }

    public ParentAwareDtoMappingContextBuilder(DtoMappingContext mc) {
        super(mc);
    }

    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.add(ParentAwareDtoEndpoint.FIND_ALL_OF_PARENT);
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(ParentAwareDtoEndpoint.FIND_ALL_OF_PARENT);
        return findEndpoints;
    }
}
