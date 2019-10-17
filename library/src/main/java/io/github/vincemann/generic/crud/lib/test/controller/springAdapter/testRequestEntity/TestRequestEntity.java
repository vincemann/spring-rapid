package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;


/**
 * For every Entity in every test in {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT}
 * this object can be created.
 * This is created by {@link TestRequestEntityFactory}.
 * For further customization one can specify its own {@link TestRequestEntity} in a TestBundle (see {@link UpdatableSucceedingTestEntityBundle}, {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestEntityBundle}, {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.FindAllTestBundle}).
 * The implementation of {@link TestRequestEntityFactory} will decide, in which way the provided {@link TestRequestEntity} in the bundle, will be taken into account.
 */
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
            headers = new LinkedMultiValueMap<>();
        }else {
            this.headers = headers;
        }
        this.expectedHttpStatus = expectedHttpStatus;
    }
}
