package io.github.vincemann.generic.crud.lib.service;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
public class EndpointService {


    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public EndpointService(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public void addMapping(RequestMappingInfo requestMappingInfo, Method requestMethod, Object controller)  {

        /*RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(urlPath)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();*/

        requestMappingHandlerMapping.
                registerMapping(requestMappingInfo, controller,
                        requestMethod
                );
    }

}