package io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.test.controller.BaseAddressProvider;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.UpdateControllerTestConfiguration;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

public class UpdateUrlParamIdRequestEntityFactory<Id extends Serializable>
        extends AbstractUrlParamIdRequestEntityFactory<Id, UpdateControllerTestConfiguration<?,Id>,SpringAdapterDtoCrudController<?,Id>> {
    public static final Map.Entry<String, String> FULL_UPDATE_QUERY_PARAM_ENTRY = new AbstractMap.SimpleEntry<>("full", "true");

    public UpdateUrlParamIdRequestEntityFactory(SpringAdapterDtoCrudController<?, Id> controller, String entityIdParamKey, BaseAddressProvider baseAddressProvider) {
        super(controller, entityIdParamKey, baseAddressProvider);
    }

    @Override
    public RequestEntity<?> create(UpdateControllerTestConfiguration<?, Id> config, Object body, Id id) {
        Boolean fullUpdate = config.getFullUpdate();
        UriComponentsBuilder uriBuilder = buildUri(getController().getUpdateMethodName(), config, id);
        if(fullUpdate!=null){
            if(fullUpdate){
                //...?full=true
                uriBuilder.queryParam(FULL_UPDATE_QUERY_PARAM_ENTRY.getKey(),FULL_UPDATE_QUERY_PARAM_ENTRY.getValue());
            }
        }
        return createRequestEntity(config,body,uriBuilder.build().toUri());
    }
}
