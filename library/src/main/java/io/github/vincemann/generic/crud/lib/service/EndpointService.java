package io.github.vincemann.generic.crud.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
@Service
public class EndpointService {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

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