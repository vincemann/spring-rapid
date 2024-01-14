package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

@Getter
public class UrlExtension {

    Class<? extends ArgAware> extensionType;
    String[] args;

    public UrlExtension(Class<? extends ArgAware> extensionType, String... args) {
        this.extensionType = extensionType;
        this.args = args;
    }

//    public String getUrlParamKey(){
//        if (QueryFilter.class.isAssignableFrom(this.getExtensionType())){
//            return GenericCrudController.QUERY_FILTER_URL_KEY;
//        }
//        else if (EntityFilter.class.isAssignableFrom(this.getExtensionType())){
//            return GenericCrudController.ENTITY_FILTER_URL_KEY;
//        }else if (EntitySortingStrategy.class.isAssignableFrom(this.getExtensionType())){
//            return GenericCrudController.ENTITY_SORTING_STRATEGY_URL_KEY;
//        }else{
//            throw new IllegalArgumentException("unknown extension type: " + this.getExtensionType());
//        }
//    }

}
