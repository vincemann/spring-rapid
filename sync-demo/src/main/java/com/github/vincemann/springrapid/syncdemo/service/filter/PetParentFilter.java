package com.github.vincemann.springrapid.syncdemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.ParentFilter;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PetParentFilter extends ParentFilter {

    public PetParentFilter() {
        super("owner");
    }
}
