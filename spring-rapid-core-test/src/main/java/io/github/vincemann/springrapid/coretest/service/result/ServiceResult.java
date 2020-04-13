package io.github.vincemann.springrapid.coretest.service.result;


import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.coretest.service.request.ServiceRequest;
import lombok.*;

/**
 * Represents the result of {@link CrudService} method in a test scenario.
 * @see ServiceRequest
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ServiceResult {
    private Exception raisedException;
    private Object result;
    private ServiceRequest serviceRequest;

    public boolean wasSuccessful(){
        return raisedException==null;
    }

    public <T> T getResult(){
        try {
            return (T)result;
        }catch (ClassCastException e) {
            throw new IllegalArgumentException("Wrong Service Result Handler/Matcher chosen");
        }
    }
}
