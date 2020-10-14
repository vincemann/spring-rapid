package com.github.vincemann.springrapid.core.controller.parentAware;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParentAwareDtoMappingContextBuilder extends DtoMappingContextBuilder {


    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.add(getCoreProperties().controller.endpoints.findAllOfParent);
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(getCoreProperties().controller.endpoints.findAllOfParent);
        return findEndpoints;
    }
}
