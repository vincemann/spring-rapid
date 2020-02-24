package io.github.vincemann.generic.crud.lib.test.service.result;


import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequest;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public abstract class ServiceResult<T> {
    private Exception raisedException;
    private T result;
    private ServiceRequest serviceRequest;
}
