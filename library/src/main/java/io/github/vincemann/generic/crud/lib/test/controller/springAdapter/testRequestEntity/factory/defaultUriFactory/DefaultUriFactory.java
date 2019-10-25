package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.testBaseUrlProvider.BaseUrlProvider;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public interface DefaultUriFactory {
    URI createDefaultUri(String methodName, Object id, MultiValueMap<String,String> additionalQueryParams);
    void setBaseUrlProvider(BaseUrlProvider baseUrlProvider);
}
