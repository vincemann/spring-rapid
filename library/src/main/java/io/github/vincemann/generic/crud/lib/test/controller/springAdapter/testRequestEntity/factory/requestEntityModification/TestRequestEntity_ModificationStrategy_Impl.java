package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification;

import org.springframework.web.util.UriComponentsBuilder;

public class TestRequestEntity_ModificationStrategy_Impl implements TestRequestEntity_ModificationStrategy {
    @Override
    public void process(TestRequestEntity requestEntity, TestRequestEntity_Modification... modifications) {
        boolean httpStatusSetByMod = false;
        for (TestRequestEntity_Modification modification : modifications) {
            if(modification==null){
                continue;
            }
            if(modification.getExpectedHttpStatus()!=null){
                if(httpStatusSetByMod){
                    throw new IllegalArgumentException("Expected HttpStatus changed multiple times by modification");
                }
                requestEntity.setExpectedHttpStatus(modification.getExpectedHttpStatus());
                httpStatusSetByMod=true;
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
}
