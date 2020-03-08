package io.github.vincemann.generic.crud.lib.test.controller;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class UrlParamIdMvcControllerTest<S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends MvcControllerTest<S, E, Id> {

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String entityIdParamKey;

    public UrlParamIdMvcControllerTest(String url) {
        super(url);
    }

    public UrlParamIdMvcControllerTest() {
    }

    public ResultActions performDelete(Id id) throws Exception {
        return getMockMvc().perform(delete(getDeleteUrl())
        .param(entityIdParamKey,id.toString()));
    }

    public ResultActions performFind(Id id) throws Exception {
        return getMockMvc().perform(get(getFindUrl())
                .param(entityIdParamKey,id.toString()));
    }



}
