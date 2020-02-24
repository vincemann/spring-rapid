package io.github.vincemann.generic.crud.lib.test.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Baseclass for all Controller IntegrationTests
 * @param <E>
 * @param <Id>
 */
@Getter
@Setter
@Slf4j
public abstract class ControllerIntegrationTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends InitializingTest
            implements InitializingBean {

    private static final String LOCAL_HOST = "http://127.0.0.1";


    @LocalServerPort
    private String port;
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext<Id> dtoMappingContext;
    private String url;
    private CrudService<E,Id, ? extends CrudRepository<E,Id>> testService;
    private SpringAdapterDtoCrudController<E, Id> controller;

    public ControllerIntegrationTest(String url) {
        this.url=url;
    }

    public ControllerIntegrationTest() {
        this(LOCAL_HOST);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        //user might want to inject own beans that are diff from controllers beans -> null checks
        if(dtoMappingContext ==null) {
            dtoMappingContext = getController().getDtoMappingContext();
        }
        if (testService == null) {
            setTestService(getController().getCastedCrudService());
        }
    }



    public <C extends SpringAdapterDtoCrudController<E,Id>> C getCastedController(){
        return (C) controller;
    }

    public String getUrlWithPort(){
        return url+":"+port;
    }

}
