package com.github.vincemann.springrapid.coredemo.service.sort;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Sort;

@Component
public class LastNameDescSorting implements SortingExtension {

    @Override
    public String getName() {
        return "name-desc";
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {

    }

    @Override
    public Sort getSort() {
        return Sort.by("lastName").descending();
    }
}
