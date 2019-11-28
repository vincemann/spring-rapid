package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.baseUrl.BaseAddress_Provider;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public interface UrlParamId_DefaultUri_Factory {
    URI createDefaultUri(String methodName, String baseUrl, Object id, @Nullable MultiValueMap<String,String> additionalQueryParams);
    void setBaseAddressProvider(BaseAddress_Provider baseAddressProvider);
}
