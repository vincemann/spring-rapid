package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import com.google.common.collect.Sets;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.core.controller.UrlParamWebExtensionParser.getUrlParamKey;
import static com.github.vincemann.springrapid.core.controller.WebExtensionType.*;

public class RapidTestUtil {

    public static SecurityContext createMockSecurityContext(RapidPrincipal principal){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }


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

    public static String createExtensionsString(List<UrlWebExtension> extensions, ApplicationContext applicationContext){
        StringBuilder sb = new StringBuilder();
        int filterCount = 0;
        for (UrlWebExtension extension : extensions) {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(extension.getExtensionType());
            Assert.isTrue(beanNamesForType.length == 1,"no single bean found with type: " + extension.getExtensionType().getSimpleName() + ". Found beanNames: " + Arrays.toString(beanNamesForType));
            WebExtension bean = (WebExtension) applicationContext.getBean(beanNamesForType[0]);
            Assert.notNull(bean,"cant find web extension bean with name " + beanNamesForType[0]);
            sb.append(bean.getName());
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

    public static void addUrlExtensionsToRequest(ApplicationContext applicationContext, MockHttpServletRequestBuilder requestBuilder, UrlWebExtension... extensions){
        List<UrlWebExtension> queryFilters = findExtensionsOfSubType(Lists.newArrayList(extensions), QueryFilter.class);
        List<UrlWebExtension> entityFilters = findExtensionsOfSubType(Lists.newArrayList(extensions), EntityFilter.class);
        List<UrlWebExtension> sortingStrategies = findExtensionsOfSubType(Lists.newArrayList(extensions), SortingExtension.class);

        Assert.isTrue(extensions.length == queryFilters.size()+entityFilters.size()+sortingStrategies.size());

        if (!queryFilters.isEmpty()){
            requestBuilder.param(getUrlParamKey(QUERY_FILTER),createExtensionsString(queryFilters,applicationContext));
        }
        if (!entityFilters.isEmpty()){
            requestBuilder.param(getUrlParamKey(ENTITY_FILTER),createExtensionsString(entityFilters,applicationContext));
        }
        if (!sortingStrategies.isEmpty()){
            requestBuilder.param(getUrlParamKey(SORTING),createExtensionsString(sortingStrategies,applicationContext));
        }
    }


    protected static List<UrlWebExtension> findExtensionsOfSubType(List<UrlWebExtension> extensions, Class<? extends WebExtension> type){
        return extensions.stream().filter(e -> type.isAssignableFrom(e.getExtensionType())).collect(Collectors.toList());
    }

}
