package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public abstract class AbstractEntityController<E extends IdentifiableEntity<ID>, ID extends Serializable>
        implements ApplicationListener<ContextRefreshedEvent>, InitializingBean, ApplicationContextAware
{

    public static final String QUERY_FILTER_URL_KEY = "qfilter";
    public static final String ENTITY_FILTER_URL_KEY = "filter";
    public static final String ENTITY_SORTING_STRATEGY_URL_KEY = "sort";

    protected Class<E> entityClass;
    protected Class<ID> idClass;
    protected String baseUrl;
    protected String entityBaseUrl;
    protected String urlEntityName;
    protected CoreProperties coreProperties;
    protected EndpointService endpointService;
    protected JsonMapper jsonMapper;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public AbstractEntityController() {
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.idClass = (Class<ID>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initUrls();
    }

    protected void initUrls() {
        this.urlEntityName = createUrlEntityName();
        this.baseUrl = createBaseUrl();
        this.entityBaseUrl = baseUrl + "/" + urlEntityName + "/";
    }

    /**
     * extracts entity filters, queryFilters or EntitySortingStrategies from http request url parameter
     * i.E.:
     *  REQUEST URL: /api/core/...?filter=filter1:arg1:arg2,filter2,filter3:myarg&sort=sortById
     */
    protected  <F extends ArgAware> List<F> extractArgAwareExtension(HttpServletRequest request, String urlParamKey) throws BadEntityException {
        String extensionParam = request.getParameter(urlParamKey);
        List<F> extensions = new ArrayList<>();

        // Check if the "filter" parameter is not null and not empty
        if (extensionParam != null && !extensionParam.isEmpty()) {
            // Split the parameter value into individual filter bean names
            for (String extensionString : extensionParam.split(",")) {
                try {
                    String[] extensionElements = extensionString.split(":");
                    String beanName = extensionElements[0];
                    F filter = (F) applicationContext.getBean(beanName);
                    if (extensionElements.length > 1) {
                        // Create a new array with length-1 elements
                        String[] args = new String[extensionElements.length - 1];
                        // Copy elements from the original array starting from index 1 to the new array
                        System.arraycopy(extensionElements, 1, args, 0, extensionElements.length - 1);
                        filter.setArgs(args);
                    }
                    extensions.add(filter);
                } catch (NoSuchBeanDefinitionException e) {
                    throw new BadEntityException("No extension bean found with name: " + extensionString);
                } catch (ClassCastException e) {
                    throw new BadEntityException("Extension bean not applicable for entity type: " + extensionString);
                }

            }
        }
        return extensions;
    }


    protected abstract void registerEndpoints() throws NoSuchMethodException;

    protected void registerEndpoint(RequestMappingInfo requestMappingInfo, String methodName) throws NoSuchMethodException {
        log.debug("Exposing " + methodName + " Endpoint for " + this.getClass().getSimpleName());
        endpointService.addMapping(requestMappingInfo,
                this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class), this);
    }

    protected void registerViewEndpoint(RequestMappingInfo requestMappingInfo, String methodName) throws NoSuchMethodException {
        log.debug("Exposing " + methodName + " Endpoint for " + this.getClass().getSimpleName());
        endpointService.addMapping(requestMappingInfo,
                this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Model.class), this);
    }

    protected String createUrlEntityName() {
        return entityClass.getSimpleName().toLowerCase();
    }

    /**
     * Override this if your API for this controller changes.
     * e.g. from /api/v1 to /api/v2
     *
     * @return
     */
    protected String createBaseUrl() {
        return coreProperties.baseUrl;
    }


    protected String readBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            registerEndpoints();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void injectJsonMapper(JsonMapper mapper) {
        this.jsonMapper = mapper;
    }


    @Autowired
    public void injectCoreProperties(CoreProperties properties) {
        this.coreProperties = properties;
    }

    @Autowired
    public void injectEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }
}
