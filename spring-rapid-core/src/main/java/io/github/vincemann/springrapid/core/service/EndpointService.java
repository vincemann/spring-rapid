package io.github.vincemann.springrapid.core.service;

import io.github.vincemann.springrapid.core.advice.log.LogInteraction;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Service that is internally used to register a controller method dynamically.
 */
public class EndpointService {


    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public EndpointService(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @LogInteraction
    public void addMapping(RequestMappingInfo requestMappingInfo, Method requestMethod, Object controller)  {

        /*RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(urlPath)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();*/

        requestMappingHandlerMapping.
                registerMapping(requestMappingInfo, controller,
                        requestMethod
                );
    }

}