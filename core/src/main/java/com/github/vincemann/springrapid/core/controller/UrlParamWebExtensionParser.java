package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UrlParamWebExtensionParser implements WebExtensionParser, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }



    @Override
    public List<WebExtension<?>> parse(HttpServletRequest request, Set<WebExtension> extensions, WebExtensionType type) throws BadEntityException {
        String extensionParam = request.getParameter(getUrlParamKey(type));
        List<WebExtension<?>> result = new ArrayList<>();

        // Check if the "filter" parameter is not null and not empty
        if (extensionParam != null && !extensionParam.isEmpty()) {
            // Split the parameter value into individual filter bean names
            for (String extensionString : extensionParam.split(",")) {
                try {
                    String[] extensionElements = extensionString.split(":");
                    String extensionName = extensionElements[0];
                    List<WebExtension<?>> matching = (List<WebExtension<?>>) (Object) extensions.stream()
                            .filter(e -> e.getName().equals(extensionName))
                            .collect(Collectors.toList());
                    if (matching.isEmpty())
                        throw new BadEntityException("No extension found for name: " + extensionName);
                    if (matching.size() > 1)
                        throw new BadEntityException("Multiple extensions found with name: " + extensionName);

                    WebExtension<?> extension = matching.get(0);
                    // create new bean if scope is prototype
                    WebExtension<?> extensionBean = (WebExtension<?>) applicationContext.getBean(extension.getClass());
                    if (extensionElements.length > 1) {
                        // Create a new array with length-1 elements
                        String[] args = new String[extensionElements.length - 1];
                        // Copy elements from the original array starting from index 1 to the new array
                        System.arraycopy(extensionElements, 1, args, 0, extensionElements.length - 1);
                        extensionBean.setArgs(args);
                    }
                    result.add(extensionBean);
                } catch (NoSuchBeanDefinitionException e) {
                    throw new BadEntityException("No extension bean found with name: " + extensionString);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Extension bean not applicable for entity type: " + extensionString);
                }

            }
        }
        return result;
    }

    public static String getUrlParamKey(WebExtensionType extensionType){
        switch (extensionType){
            case SORTING:
                return "sort";
            case QUERY_FILTER:
                return "qfilter";
            case ENTITY_FILTER:
                return "filter";
        }
        throw new IllegalArgumentException("unknown extension type: " + extensionType);
    }
}
