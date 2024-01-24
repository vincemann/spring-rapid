package com.github.vincemann.springrapid.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class HttpServletRequestUtils {


    public static List<String> getRequestParameterKeysWithoutValue(HttpServletRequest request) {
        List<String> keysWithNoValue = new ArrayList<>();
        String queryString = request.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            return keysWithNoValue;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=", -1); // -1 limit to include empty trailing strings

            // Check if the keyValuePair array has only one element, meaning no value is present.
            if (keyValuePair.length == 1 || keyValuePair[1].isEmpty()) {
                keysWithNoValue.add(keyValuePair[0]);
            }
        }

        return keysWithNoValue;
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
