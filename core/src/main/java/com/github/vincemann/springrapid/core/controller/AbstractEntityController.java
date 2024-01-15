package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.UrlExtension;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.Getter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    protected Set<UrlExtension> extensions = new HashSet<>();

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
     * call this method in order to add whitelisted {@link UrlExtension}.
     * Given extensions are only used to retrieve {@link UrlExtension#getName()} and getClass.
     * When extension matches request, applicationContext.getBean(extension.getClass()) is executed and bean
     * retrieved from context will be used - you can use {@link org.springframework.context.annotation.Scope} Prototype
     * if you want a new instance of extension to be created for each request.
     *
     * Example:
     *
     * @Autowired
     * public void configureAllowedExtensions(ModuleParentFilter parentFilter) {
     *     setAllowedExtensions(parentFilter);
     * }
     *
     * or
     *
     * public void MyController() {
     *     super()
     *     setAllowedExtensions(new ModuleParentFilter());
     * }
     */
    protected void addAllowedExtensions(UrlExtension... extensions){
        this.extensions.addAll(Lists.newArrayList(extensions));
    }

    /**
     * extracts entity filters, queryFilters or EntitySortingStrategies from http request url parameter
     * i.E.:
     *  REQUEST URL: /api/core/...?filter=filter1:arg1:arg2,filter2,filter3:myarg&sort=sortById
     */
    protected  <Ext extends UrlExtension> List<Ext> extractExtensions(HttpServletRequest request, String urlParamKey) throws BadEntityException {
        String extensionParam = request.getParameter(urlParamKey);
        List<Ext> result = new ArrayList<>();

        // Check if the "filter" parameter is not null and not empty
        if (extensionParam != null && !extensionParam.isEmpty()) {
            // Split the parameter value into individual filter bean names
            for (String extensionString : extensionParam.split(",")) {
                try {
                    String[] extensionElements = extensionString.split(":");
                    String extensionName = extensionElements[0];
                    List<Ext> matching = (List<Ext>) extensions.stream()
                            .filter(e -> e.getName().equals(extensionName))
                            .collect(Collectors.toList());
                    if (matching.isEmpty())
                        throw new BadEntityException("No extension found for name: " + extensionName);
                    if (matching.size() > 1)
                        throw new BadEntityException("Multiple extensions found with name: " + extensionName);

                    Ext extension = matching.get(0);
                    // create new bean if scope is prototype
                    Ext extensionBean = (Ext) applicationContext.getBean(extension.getClass());
                    if (extensionElements.length > 1) {
                        // Create a new array with length-1 elements
                        String[] args = new String[extensionElements.length - 1];
                        // Copy elements from the original array starting from index 1 to the new array
                        System.arraycopy(extensionElements, 1, args, 0, extensionElements.length - 1);
                        extensionBean.setArgs(args);
                    }
                    result.add(extensionBean);
                } catch (NoSuchBeanDefinitionException e) {
                    throw new BadEntityException("No extension bean found with name: " + extensionString);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Extension bean not applicable for entity type: " + extensionString);
                }

            }
        }
        return result;
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
