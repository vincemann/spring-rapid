package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.testBaseUrlProvider.BaseAddressProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public interface UrlParamIdDefaultUriFactory {
    URI createDefaultUri(String methodName, String baseUrl, Object id, @Nullable MultiValueMap<String,String> additionalQueryParams);
    void setBaseAddressProvider(BaseAddressProvider baseAddressProvider);
}
