package com.github.vincemann.springrapid.syncdemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.ParentFilter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PetParentFilter extends ParentFilter<Long> {

    public PetParentFilter() {
        super("owner");
    }

    @Override
    public String toString() {
        return "PetParentFilter{" +
                "parentId=" + getParentId() +
                '}';
    }
}
