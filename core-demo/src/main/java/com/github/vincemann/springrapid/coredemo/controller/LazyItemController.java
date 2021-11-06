package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.parentAware.ParentAwareCrudController;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.model.LazyItem;
import com.github.vincemann.springrapid.coredemo.service.LazyItemService;

@WebController
public class LazyItemController extends CrudController<LazyItem,Long, LazyItemService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(LazyItem.class).build();
    }
}
