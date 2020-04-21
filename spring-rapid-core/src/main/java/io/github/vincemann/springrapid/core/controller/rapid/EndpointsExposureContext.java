package io.github.vincemann.springrapid.core.controller.rapid;

import io.github.vincemann.springrapid.core.slicing.components.WebComponent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
/**
 * Gives User fine grained control about which endpoints should be exposed by {@link RapidController}.
 */
public class EndpointsExposureContext {
    private boolean createEndpointExposed=true;
    private boolean findEndpointExposed =true;
    private boolean updateEndpointExposed=true;
    private boolean deleteEndpointExposed=true;
    private boolean findAllEndpointExposed=true;

    @Builder
    public EndpointsExposureContext(Boolean createEndpointExposed, Boolean findEndpointExposed, Boolean updateEndpointExposed, Boolean deleteEndpointExposed, Boolean findAllEndpointExposed) {
        if(createEndpointExposed ==null){
            this.createEndpointExposed=true;
        }else {
            this.createEndpointExposed = createEndpointExposed;
        }

        if(findEndpointExposed ==null){
            this.findEndpointExposed =true;
        }else {
            this.findEndpointExposed = findEndpointExposed;
        }

        if(deleteEndpointExposed ==null){
            this.deleteEndpointExposed=true;
        }else {
            this.deleteEndpointExposed = deleteEndpointExposed;
        }


        if(updateEndpointExposed ==null){
            this.updateEndpointExposed=true;
        }else {
            this.updateEndpointExposed = updateEndpointExposed;
        }

        if(findAllEndpointExposed ==null){
            this.findAllEndpointExposed=true;
        }else {
            this.findAllEndpointExposed = findAllEndpointExposed;
        }
    }

    public EndpointsExposureContext() {
    }
}
