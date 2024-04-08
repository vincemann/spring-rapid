package com.github.vincemann.springrapid.auth.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Service that is internally used to register controller methods dynamically.
 */
public class EndpointService {

    private final Log log = LogFactory.getLog(getClass());


    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public EndpointService(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public void addMapping(RequestMappingInfo requestMappingInfo, Method requestMethod, Object controller)  {
        log.debug(LogMessage.format("registering endpoint: '%s'",requestMappingInfo.toString()));

        requestMappingHandlerMapping.
                registerMapping(requestMappingInfo, controller,
                        requestMethod
                );
    }

}