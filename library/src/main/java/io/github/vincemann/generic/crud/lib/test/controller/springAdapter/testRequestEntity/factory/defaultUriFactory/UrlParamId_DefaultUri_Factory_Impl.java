package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.baseUrl.BaseAddress_Provider;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UrlParamId_DefaultUri_Factory_Impl implements UrlParamId_DefaultUri_Factory {

    private BaseAddress_Provider baseAddressProvider;
    private String entityIdParamKey;

    public UrlParamId_DefaultUri_Factory_Impl(String entityIdParamKey) {
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public void setBaseAddressProvider(BaseAddress_Provider baseAddressProvider) {
        this.baseAddressProvider = baseAddressProvider;
    }



    @Override
    public URI createDefaultUri(String methodName, String baseUrl, Object id,MultiValueMap<String, String> additionalQueryParams) {
        String baseAddress = getBaseAddressProvider().provideAddress();
        UriComponentsBuilder builder;
        if(id!=null) {
            builder= UriComponentsBuilder.fromHttpUrl(baseAddress + baseUrl + methodName)
                    .queryParam(entityIdParamKey, id);
        }else {
            builder = UriComponentsBuilder.fromHttpUrl(baseAddress + baseUrl + methodName);
        }
        if(additionalQueryParams!=null){
            additionalQueryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }

    public BaseAddress_Provider getBaseAddressProvider() {
        if(baseAddressProvider ==null){
            throw new IllegalStateException("No Base Url Provider set yet");
        }
        return baseAddressProvider;
    }

}
