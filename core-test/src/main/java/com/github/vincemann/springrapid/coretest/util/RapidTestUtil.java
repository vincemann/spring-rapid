package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coretest.controller.UrlExtension;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RapidTestUtil {

    public static String[] dtoIdProperties(Class<?> entityClass) {
        List<String> idFields = new ArrayList<>();
        ReflectionUtils.doWithFields(entityClass, field -> {
            if (IdPropertyNameUtils.isIdField(field.getName()) ||
                    IdPropertyNameUtils.isCollectionIdField(field.getName())){
                idFields.add(field.getName());
            }
        });
        return Sets.newHashSet(idFields).toArray(new String[0]);
    }

    public static String createUpdateJsonLine(String operation, String path, String value){
        return "  {\"op\": \""+operation+"\", \"path\": \""+path+"\", \"value\": \""+value+"\"}";
    }

    public static String createUpdateJsonLine(String operation, String path){
        return "  {\"op\": \""+operation+"\", \"path\": \""+path+"\"}";
    }

    public static String createUpdateJsonRequest(String... lines){
        StringBuilder sb = new StringBuilder()
                .append("[\n" );
        boolean oneLiner = lines.length == 1;
        boolean lastLine = false;
        int count = 0;
        for (String line : lines) {
            if (count+1==lines.length){
                lastLine=true;
            }
            sb.append(line);
            if (!oneLiner && !lastLine){
                sb.append(",");
            }
            sb.append("\n");
            count++;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String createExtensionsString(List<UrlExtension> extensions, ApplicationContext applicationContext){
        StringBuilder sb = new StringBuilder();
        int filterCount = 0;
        for (UrlExtension extension : extensions) {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(extension.getExtensionType());
            Assertions.assertEquals(1,beanNamesForType.length,"no single bean found with type: " + extension.getExtensionType().getSimpleName() + ". Found beanNames: " + Arrays.toString(beanNamesForType));
            sb.append(beanNamesForType[0]);
            int count = 0;
            String[] args = extension.getArgs();
            for (String arg : args) {
                if (count++ != args.length)
                    sb.append(":");
                sb.append(arg);
            }
            if (++filterCount < extensions.size()){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void addUrlExtensionsToRequest(ApplicationContext applicationContext, MockHttpServletRequestBuilder requestBuilder, UrlExtension... extensions){
        List<UrlExtension> queryFilters = findExtensionsOfSubType(Lists.newArrayList(extensions), QueryFilter.class);
        List<UrlExtension> entityFilters = findExtensionsOfSubType(Lists.newArrayList(extensions), EntityFilter.class);
        List<UrlExtension> sortingStrategies = findExtensionsOfSubType(Lists.newArrayList(extensions), EntitySortingStrategy.class);

        Assertions.assertEquals(extensions.length,queryFilters.size()+entityFilters.size()+sortingStrategies.size());

        if (!queryFilters.isEmpty()){
            requestBuilder.param(GenericCrudController.QUERY_FILTER_URL_KEY,createExtensionsString(queryFilters,applicationContext));
        }
        if (!entityFilters.isEmpty()){
            requestBuilder.param(GenericCrudController.ENTITY_FILTER_URL_KEY,createExtensionsString(entityFilters,applicationContext));
        }
        if (!sortingStrategies.isEmpty()){
            requestBuilder.param(GenericCrudController.ENTITY_SORTING_STRATEGY_URL_KEY,createExtensionsString(sortingStrategies,applicationContext));
        }
    }


    protected static List<UrlExtension> findExtensionsOfSubType(List<UrlExtension> extensions, Class<? extends ArgAware> type){
        return extensions.stream().filter(e -> type.isAssignableFrom(e.getExtensionType())).collect(Collectors.toList());
    }

}
