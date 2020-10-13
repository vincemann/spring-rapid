package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.github.vincemann.springrapid.core.controller.CrudController;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public interface UrlParamIdRapidControllerTest
        <C extends CrudController<?,Id,?>,
        Id extends Serializable>
            extends MvcRapidControllerTest<C> {

    public default MockHttpServletRequestBuilder delete(Id id) throws Exception {
        return MockMvcRequestBuilders.delete(getDeleteUrl())
                /*.contentType(getContentType())*/
                .param("id",id.toString());
    }

    public default MockHttpServletRequestBuilder find(Id id) throws Exception {
        return get(getFindUrl())
                /*.contentType(getContentType())*/
                .param("id",id.toString());
    }

    public default MockHttpServletRequestBuilder update(String patchString,Id id) throws Exception {
//        String fullUpdateQueryParam = getController().getFullUpdateQueryParam();

        return put(getUpdateUrl())
                .param("id",id.toString())
                .content(patchString)
                .contentType(getController().getMediaType());
    }
}
