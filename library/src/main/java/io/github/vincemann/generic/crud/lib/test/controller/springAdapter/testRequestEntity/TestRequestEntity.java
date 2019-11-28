package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;



@Getter
@Setter
public class TestRequestEntity {

    private RequestMethod method;
    private URI url;
    private MultiValueMap<String,String> headers;
    private HttpStatus expectedHttpStatus;


    @Builder
    public TestRequestEntity(RequestMethod method, URI url, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus) {
        this.method = method;
        this.url = url;
        if(headers==null){
            this.headers = new LinkedMultiValueMap<>();
        }else {
            this.headers = headers;
        }
        this.expectedHttpStatus = expectedHttpStatus;
    }
}
