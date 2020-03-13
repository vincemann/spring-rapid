package io.github.vincemann.generic.crud.lib.test.controller;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public abstract class UrlParamIdMvcControllerTest<S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends MvcCrudControllerTest<S, E, Id> {

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String entityIdParamKey;


    public MockHttpServletRequestBuilder delete(Id id) throws Exception {
        return MockMvcRequestBuilders.delete(getDeleteUrl())
        .param(entityIdParamKey,id.toString());
    }

    public MockHttpServletRequestBuilder find(Id id) throws Exception {
        return get(getFindUrl())
                .param(entityIdParamKey,id.toString());
    }



}
