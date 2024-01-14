package com.github.vincemann.springrapid.core.controller.parent;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.AbstractDtoMappingContextBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParentAwareDtoMappingContextBuilder extends AbstractDtoMappingContextBuilder<ParentAwareCrudController, ParentAwareDtoMappingContextBuilder> {


    public ParentAwareDtoMappingContextBuilder(ParentAwareCrudController controller) {
        super(controller);
    }

    @Override
    protected List<String> getAllEndpoints() {
        List<String> allEndpoints = super.getAllEndpoints();
        allEndpoints.add(getController().getFindAllOfParentUrl());
        return allEndpoints;
    }

    @Override
    protected List<String> getFindEndpoints() {
        List<String> findEndpoints = super.getFindEndpoints();
        findEndpoints.add(getController().getFindAllOfParentUrl());
        return findEndpoints;
    }
}
