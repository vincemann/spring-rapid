package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.ArgAwareFilter;
import com.github.vincemann.springrapid.core.service.EntityFilter;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class HttpServletRequestUtils {


    /**
     * extracts entity filters from http request url parameter
     *  REQUEST URL: /api/core/...?filter=filter1:arg1:arg2,filter2,filter3:myarg
     */
    public static <F extends ArgAwareFilter> List<F> extractFilters(HttpServletRequest request, ApplicationContext applicationContext, String filterParamKey) throws BadEntityException {
        String filterParam = request.getParameter(filterParamKey);
        List<F> filters = new ArrayList<>();

        // Check if the "filter" parameter is not null and not empty
        if (filterParam != null && !filterParam.isEmpty()) {
            // Split the parameter value into individual filter bean names
            for (String filterString : filterParam.split(",")) {
                try {
                    String[] filterElements = filterString.split(":");
                    String beanName = filterElements[0];
                    F filter = (F) applicationContext.getBean(beanName);
                    if (filterElements.length > 1) {
                        // Create a new array with length-1 elements
                        String[] args = new String[filterElements.length - 1];
                        // Copy elements from the original array starting from index 1 to the new array
                        System.arraycopy(filterElements, 1, args, 0, filterElements.length - 1);
                        filter.setArgs(args);
                    }
                    filters.add(filter);
                } catch (NoSuchBeanDefinitionException e) {
                    throw new BadEntityException("No filter bean found with name: " + filterString);
                } catch (ClassCastException e) {
                    throw new BadEntityException("Filter bean not applicable for entity type: " + filterString);
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
