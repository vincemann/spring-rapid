package io.github.vincemann.generic.crud.lib.test.service.result;


import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequest;
import lombok.*;

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
