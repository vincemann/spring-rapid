package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
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
public abstract class AbstractEntityController
        <
                E extends IdentifiableEntity<ID>,
                ID extends Serializable
        >
        implements ApplicationListener<ContextRefreshedEvent>, InitializingBean, ApplicationContextAware
{


    protected Class<E> entityClass;
    protected Class<ID> idClass;
    protected String baseUrl;
    protected String entityBaseUrl;
    protected String urlEntityName;
    protected CoreProperties coreProperties;
    protected EndpointService endpointService;
    protected JsonMapper jsonMapper;
    protected WebExtensionParser extensionParser;
    private List<String> ignoredEndPoints = new ArrayList<>();

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
     * call this method in order to add whitelisted {@link WebExtension}.
     * Given extensions are only used to retrieve {@link WebExtension#getName()} and getClass.
     * When extension matches request, applicationContext.getBean(extension.getClass()) is executed and bean
     * retrieved from context will be used - you can use {@link org.springframework.context.annotation.Scope} Prototype
     * if you want a new instance of extension to be created for each request.
     *
     * Example:
     *
     * @Autowired
     * public void registerAllowedExtensions(ModuleParentFilter parentFilter) {
     *     registerExtensions(parentFilter);
     * }
     *
     * or
     *
     * public void MyController() {
     *     super()
     *     registerExtensions(new ModuleParentFilter());
     * }
     */
    protected void registerExtensions(WebExtension<? super E>... extensions){
        extensionParser.registerExtensions(extensions);
    }


    protected  <Ext extends WebExtension<? super E>> List<Ext> extractExtensions(HttpServletRequest request, WebExtensionType type) throws BadEntityException {
        return (List<Ext>) extensionParser.parse(request,type);
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
            throw new IllegalArgumentException("could not register endpoints, method not found",e);
        }
    }

    @Autowired
    public void setJsonMapper(JsonMapper mapper) {
        this.jsonMapper = mapper;
    }


    @Autowired
    public void setExtensionParser(WebExtensionParser extensionParser) {
        this.extensionParser = extensionParser;
    }

    @Autowired
    public void setCoreProperties(CoreProperties properties) {
        this.coreProperties = properties;
    }

    @Autowired
    public void setEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }
}
