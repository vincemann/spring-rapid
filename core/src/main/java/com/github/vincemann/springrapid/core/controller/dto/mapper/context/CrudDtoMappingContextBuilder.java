package com.github.vincemann.springrapid.core.controller.dto.mapper.context;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrudDtoMappingContextBuilder extends AbstractDtoMappingContextBuilder<GenericCrudController, CrudDtoMappingContextBuilder> {
    public CrudDtoMappingContextBuilder(GenericCrudController controller) {
        super(controller);
    }
}
