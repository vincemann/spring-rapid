package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.BaseAddressProvider;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

@Getter
@Setter
public abstract class ControllerIntegrationTestContext<E extends IdentifiableEntity<Id>, Id extends Serializable>
            implements BaseAddressProvider, InitializingBean {

    private static final String LOCAL_HOST = "http://127.0.0.1";
    private static TestRestTemplate restTemplate;
    @LocalServerPort
    private String port;
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext<Id> dtoMappingContext;
    private String url;
    private CrudService<E,Id, CrudRepository<E,Id>> testService;
    private DtoCrudController_SpringAdapter<E, Id> controller;
    private RequestEntityFactory<Id> requestEntityFactory;

    public ControllerIntegrationTestContext(String url) {
        this.url=url;
    }

    public ControllerIntegrationTestContext() {
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
            setTestService(getController().getCrudService());
        }
        this.requestEntityFactory= provideRequestEntityFactory();
    }

    @BeforeAll
    public static void setUp() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        clientHttpRequestFactory.setBufferRequestBody(false);
        restTemplate = new TestRestTemplate();
        restTemplate.getRestTemplate().setRequestFactory(clientHttpRequestFactory);
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
