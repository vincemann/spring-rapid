package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.controller.UrlParamWebExtensionParser;
import com.github.vincemann.springrapid.core.controller.WebExtensionType;
import com.github.vincemann.springrapid.core.util.Lists;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;

public class DtoMappingConditions {

    private DtoMappingConditions(){}

    public static Predicate<DtoRequestInfo> endpoint(String endpoint){
        return new DescribablePredicate<>("endpoint: " + endpoint) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return dtoRequestInfo.getEndpoint().equals(endpoint);
            }
        };
    }

    public static Predicate<DtoRequestInfo> endpointRegex(String regex) {
        return new DescribablePredicate<>("endpoint-regex: " + regex) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return dtoRequestInfo.getEndpoint().matches(regex);
            }
        };
    }


    public static Predicate<DtoRequestInfo> principal(Principal principal){
        return new DescribablePredicate<>("principal: " + principal) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return dtoRequestInfo.getPrincipal().equals(principal);
            }
        };
    }

    public static Predicate<DtoRequestInfo> direction(Direction direction){
        return new DescribablePredicate<>("direction: " + direction) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return dtoRequestInfo.getDirection().equals(direction);
            }
        };
    }

    public static Predicate<DtoRequestInfo> urlWebExtension(String extensionName, WebExtensionType type){
        return new DescribablePredicate<>("url-web-extension: " + extensionName) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                String extensionString = dtoRequestInfo.getRequest().getParameter(UrlParamWebExtensionParser.getUrlParamKey(type));
                if (extensionString != null){
                    return extensionString.contains(extensionName);
                }
                return false;
            }
        };
    }

    public static Predicate<DtoRequestInfo> roles(String... roles){
        return new DescribablePredicate<>("roles: " + Arrays.toString(roles)) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return new HashSet<>(dtoRequestInfo.getAuthorities()).containsAll(Lists.newArrayList(roles));
            }
        };
    }

    public static Predicate<DtoRequestInfo> any(){
        return new DescribablePredicate<>("any") {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return true;
            }
        };
    }

    /**
     * checks if url param keys all exist
     */
    public static Predicate<DtoRequestInfo> urlParam(String... urlParams){
        return new DescribablePredicate<>("url-params: " + Arrays.toString(urlParams)) {
            @Override
            public boolean test(DtoRequestInfo dtoRequestInfo) {
                return dtoRequestInfo.getRequest().getParameterMap().keySet().containsAll(Lists.newArrayList(urlParams));
            }
        };
    }

//    public static Predicate<DtoRequestInfo> urlParamWithValue(String... urlParams){
//        return new DescribablePredicate<>("url-params: " + Arrays.toString(urlParams)) {
//            @Override
//            public boolean test(DtoRequestInfo dtoRequestInfo) {
//                // todo implement
//            }
//        };
//    }



}
