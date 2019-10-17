package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.httpStatusFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.CrudControllerTestCase;
import org.springframework.http.HttpStatus;

public interface CrudControllerHttpStatusFactory {

    public HttpStatus createInstance(CrudControllerTestCase crudControllerTestCase);
}
