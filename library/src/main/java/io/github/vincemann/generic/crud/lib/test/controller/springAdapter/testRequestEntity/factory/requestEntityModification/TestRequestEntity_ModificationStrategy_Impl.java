package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity_Modification;
import org.springframework.web.util.UriComponentsBuilder;

public class TestRequestEntity_ModificationStrategy_Impl implements TestRequestEntity_ModificationStrategy {
    @Override
    public void modify(TestRequestEntity requestEntity, TestRequestEntity_Modification modification) {
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
