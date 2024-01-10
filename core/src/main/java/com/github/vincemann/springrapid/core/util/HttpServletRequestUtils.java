package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.EntityFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpServletRequestUtils {

    // extracts entity filters from http request url parameter
    // /.../?filter=filter1,filter2,filter3
    public static <E extends EntityFilter<?>> Set<E> extractFilters(HttpServletRequest request, ApplicationContext applicationContext) throws BadEntityException {
        String filterParam = request.getParameter("filter");
        Set<E> filters = new HashSet<>();

        // Check if the "filter" parameter is not null and not empty
        if (filterParam != null && !filterParam.isEmpty()) {
            // Split the parameter value into individual filter bean names
            for (String beanName : filterParam.split(",")) {
                try {
                    E filter = (E) applicationContext.getBean(beanName);
                    filters.add(filter);
                } catch (NoSuchBeanDefinitionException e) {
                    throw new BadEntityException("No filter bean found with name: " + beanName);
                } catch (ClassCastException e) {
                    throw new BadEntityException("Filter bean not applicable for entity type: " + beanName);
                }

            }
        }
        return filters;
    }

    public static Map<String, String[]> getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> queryParameters = new HashMap<>();
        String queryString = request.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            String[] values = queryParameters.get(keyValuePair[0]);
            values = ArrayUtils.add(values, keyValuePair.length == 1 ? "" : keyValuePair[1]); //length is one if no value is available.
            queryParameters.put(keyValuePair[0], values);
        }
        return queryParameters;
    }


    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
