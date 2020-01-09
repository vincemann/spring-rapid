package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Getter
@Setter
public class TestRequestEntity_Modification {
    private Map<String,String> additionalQueryParams;
    private MultiValueMap<String,String> headers;
    private HttpStatus expectedHttpStatus;

    @Builder
    public TestRequestEntity_Modification(Map<String, String> additionalQueryParams, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus) {
        this.additionalQueryParams = additionalQueryParams;
        this.headers = headers;
        this.expectedHttpStatus = expectedHttpStatus;
    }
}
