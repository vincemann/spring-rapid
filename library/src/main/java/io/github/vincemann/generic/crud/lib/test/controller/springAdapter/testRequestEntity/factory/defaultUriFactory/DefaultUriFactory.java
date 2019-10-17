package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import org.springframework.util.MultiValueMap;

import java.net.URI;

public interface DefaultUriFactory {
    URI createDefaultUri(String methodName, Object id, MultiValueMap<String,String> additionalQueryParams);
}
