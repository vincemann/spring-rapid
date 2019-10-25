package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import org.springframework.web.util.UriComponentsBuilder;

public class TestRequestEntityModificationStrategyImpl implements TestRequestEntityModificationStrategy {
    @Override
    public void modify(TestRequestEntity requestEntity, TestRequestEntityModification modification) {
        if(modification.getExpectedHttpStatus()!=null){
            requestEntity.setExpectedHttpStatus(modification.getExpectedHttpStatus());
        }
        if(modification.getAdditionalQueryParams()!=null) {
            //append additional query params
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(requestEntity.getUrl());
            modification.getAdditionalQueryParams().forEach(uriComponentsBuilder::queryParam);
            requestEntity.setUrl(uriComponentsBuilder.build().toUri());
        }
        if(modification.getHeaders()!=null){
            //add additional headers
            requestEntity.getHeaders().addAll(modification.getHeaders());
        }
    }
}
