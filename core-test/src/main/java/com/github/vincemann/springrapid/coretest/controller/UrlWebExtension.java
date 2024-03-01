package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Represents a container for either {@link EntityFilter}, {@link QueryFilter} or {@link SortingExtension}.
 * Can be converted to String via {@link com.github.vincemann.springrapid.coretest.util.RapidTestUtil#createExtensionsString(List, ApplicationContext)}
 * which can be added as url param for some endpoints like {@link CrudController#findAll(HttpServletRequest, HttpServletResponse)}.
 *
 * Use {@link CrudControllerTestTemplate#findAll(UrlWebExtension...)} and similar helper methods
 * to add extensions while testing, whenever possible.
 *
 * If not possible call {@link com.github.vincemann.springrapid.coretest.util.RapidTestUtil#addUrlExtensionsToRequest(ApplicationContext, MockHttpServletRequestBuilder, UrlWebExtension...)} to add
 * to mvc request.
 */
@Getter
public class UrlWebExtension {

    Class<? extends WebExtension<?>> extensionType;
    String[] args;

    public UrlWebExtension(Class<? extends WebExtension<?>> extensionType, String... args) {
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
