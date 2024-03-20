package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractEntityController
        <
                E extends IdAwareEntity<ID>,
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

    protected Validator validator;

    protected List<String> ignoredEndPoints = new ArrayList<>();
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

    protected ResponseEntity<Void> okNoContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    protected String readRequestParam(HttpServletRequest request, String key) throws BadEntityException {
        String param = request.getParameter(key);
        if (param == null) {
            throw new BadEntityException("RequestParam with key: " + key + " not found");
        } else {
            return param;
        }
    }

    protected ResponseEntity<String> ok(String jsonDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonDto);
    }

    protected Optional<String> readOptionalRequestParam(HttpServletRequest request, String key) {
        String param = request.getParameter(key);
        if (param != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }

    public void validateDto(Object dto) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(dto);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public Class<ID> getIdClass() {
        return idClass;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEntityBaseUrl() {
        return entityBaseUrl;
    }

    public String getUrlEntityName() {
        return urlEntityName;
    }

    public CoreProperties getCoreProperties() {
        return coreProperties;
    }

    public EndpointService getEndpointService() {
        return endpointService;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    public List<String> getIgnoredEndPoints() {
        return ignoredEndPoints;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
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
