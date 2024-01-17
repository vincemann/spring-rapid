package com.github.vincemann.springrapid.coredemo.service.sort;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.data.domain.Sort;

@ServiceComponent
public class LastNameAscSorting implements SortingExtension {

    @Override
    public String getName() {
        return "name-asc";
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {

    }

    @Override
    public Sort getSort() {
        return Sort.by("lastName").ascending();
    }
}