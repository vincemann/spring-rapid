package io.github.vincemann.generic.crud.lib.controller.springAdapter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
/**
 * Gives User fine grained control about which endpoints should be exposed by {@link DTOCrudControllerSpringAdapter}.
 */
public class EndpointsExposureDetails {
    private boolean createEndpointExposed=true;
    private boolean getEndpointExposed=true;
    private boolean updateEndpointExposed=true;
    private boolean deleteEndpointExposed=true;

    @Builder
    public EndpointsExposureDetails(Boolean createEndpointExposed, Boolean getEndpointExposed, Boolean updateEndpointExposed, Boolean deleteEndpointExposed) {
        if(createEndpointExposed ==null){
            this.createEndpointExposed=true;
        }else {
            this.createEndpointExposed = createEndpointExposed;
        }

        if(getEndpointExposed ==null){
            this.getEndpointExposed=true;
        }else {
            this.getEndpointExposed = getEndpointExposed;
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
    }

    public EndpointsExposureDetails() {
    }
}
