package com.github.vincemann.springrapid.coretest.controller.template;

import com.github.vincemann.springrapid.core.controller.CrudController;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.Serializable;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class CrudControllerTestTemplate<C extends CrudController>
        extends MvcControllerTestTemplate<C>
{

    public MockHttpServletRequestBuilder delete(Serializable id) throws Exception {
        return MockMvcRequestBuilders.delete(controller.getDeleteUrl())
                .param("id",id.toString());
    }

    public MockHttpServletRequestBuilder find(Serializable id) throws Exception {
        return get(controller.getFindUrl())
                .param("id",id.toString());
    }

    public <D> D find2xx(Serializable id, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(find(id),dtoClass);
    }

    public <D> D find2xx(Serializable id,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(find(id),token),dtoClass);
    }


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(controller.getCreateUrl())
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> D create2xx(Object dto, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(create(dto),dtoClass);
    }

    public <D> D create2xx(Object dto,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(create(dto),token),dtoClass);
    }

    public  MockHttpServletRequestBuilder findSome(Set<String> ids) throws Exception {
        return post(controller.getFindSomeUrl())
                .content(serialize(ids))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> Set<D> findSome2xx(Set<String> ids, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserializeToSet(findSome(ids),dtoClass);
    }
}
