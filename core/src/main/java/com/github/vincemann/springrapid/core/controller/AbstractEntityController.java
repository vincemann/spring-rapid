package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.log.LogMessage;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractEntityController
        <
                E extends IdentifiableEntity<ID>,
                ID extends Serializable
        >
        implements ApplicationListener<ContextRefreshedEvent>, InitializingBean, ApplicationContextAware
{

    protected final Log log = LogFactory.getLog(getClass());

    protected Class<E> entityClass;
    protected Class<ID> idClass;
    protected String baseUrl;
    protected String entityBaseUrl;
    protected String urlEntityName;
    protected CoreProperties coreProperties;
    protected EndpointService endpointService;
    protected ObjectMapper objectMapper;
    protected WebExtensionParser extensionParser;

    protected List<String> ignoredEndPoints = new ArrayList<>();
    protected Set<WebExtension> extensions = new HashSet<>();

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings({"all"})
    public AbstractEntityController() {
        this.entityClass = (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(),AbstractEntityController.class)[0];
        this.idClass = (Class<ID>) GenericTypeResolver.resolveTypeArguments(this.getClass(),AbstractEntityController.class)[1];
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
    protected final void registerExtensions(WebExtension<? super E>... extensions){
        log.debug(LogMessage.format("Registering extensions: %s", Arrays.stream(extensions).map(WebExtension::getName).collect(Collectors.toSet())));
        this.extensions.addAll(Sets.newHashSet(extensions));
    }


    protected <Ex extends WebExtension<? super E>> List<Ex> extractExtensions(HttpServletRequest request, WebExtensionType type) throws BadEntityException {
        return (List<Ex>) extensionParser.parse(request,extensions,type);
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
    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
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
