package com.github.vincemann.springrapid.coredemo.service.sort;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.MethodNameUtil;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.data.domain.Sort;

import static com.github.vincemann.springrapid.core.util.MethodNameUtil.propertyNameOf;

@ServiceComponent
public class LastNameDescSorting implements EntitySortingStrategy {

    @Override
    public String getName() {
        return "name-desc";
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {

    }

    @Override
    public Sort getSort() {
        return Sort.by(propertyNameOf(new Owner()::getLastName)).descending();
    }
}
