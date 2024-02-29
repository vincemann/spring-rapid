package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.VisitController;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
public class VisitControllerTestTemplate extends CrudControllerTestTemplate<VisitController> {

}
