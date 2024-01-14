package com.github.vincemann.springrapid.syncdemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.ParentFilter;
import org.springframework.stereotype.Component;

@Component("ownerFilter")
public class PetsOfOwnerFilter extends ParentFilter {

    public PetsOfOwnerFilter() {
        super("owner");
    }
}
