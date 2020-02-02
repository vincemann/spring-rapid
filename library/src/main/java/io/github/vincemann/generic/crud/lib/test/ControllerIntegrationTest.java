package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.BaseAddressProvider;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Baseclass for all Controller IntegrationTests
 * @param <E>
 * @param <Id>
 */
@Getter
@Setter
public abstract class ControllerIntegrationTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
            implements BaseAddressProvider, InitializingBean {

    private static final String LOCAL_HOST = "http://127.0.0.1";
    private static TestRestTemplate restTemplate;
    @LocalServerPort
    private String port;
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext<Id> dtoMappingContext;
    private String url;
    private CrudService<E,Id, ? extends CrudRepository<E,Id>> testService;
    private SpringAdapterDtoCrudController<E, Id> controller;
    private RequestEntityFactory<Id> requestEntityFactory;

    public ControllerIntegrationTest(String url) {
        this.url=url;
    }

    public ControllerIntegrationTest() {
        this(LOCAL_HOST);
    }

    protected abstract RequestEntityFactory<Id> provideRequestEntityFactory();

    @Override
    public void afterPropertiesSet() throws Exception {
        //user might want to inject own beans that are diff from controllers beans -> null checks
        if(dtoMappingContext ==null) {
            dtoMappingContext = getController().getDtoMappingContext();
        }
        if (testService == null) {
            setTestService(getController().getCastedCrudService());
        }
        this.requestEntityFactory= provideRequestEntityFactory();
    }

    public ControllerTestConfiguration<Id> expect(HttpStatus httpStatus){
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(httpStatus)
                .build();
    }

    @BeforeAll
    public static void setUp() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        clientHttpRequestFactory.setBufferRequestBody(false);
        restTemplate = new TestRestTemplate();
        restTemplate.getRestTemplate().setRequestFactory(clientHttpRequestFactory);
    }

    public <C extends SpringAdapterDtoCrudController<E,Id>> C getCastedController(){
        return (C) controller;
    }

    public String getUrlWithPort(){
        return url+":"+port;
    }

    @Override
    public String provideAddress() {
        return getUrlWithPort();
    }

    public TestRestTemplate getRestTemplate() {
        return restTemplate;
    }
}
