package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequest;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequestBuilder;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.action.ServiceResultActions;
import io.github.vincemann.generic.crud.lib.test.service.result.handler.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationTargetException;

@TestComponent
public class ServiceTestTemplate
    implements ApplicationContextAware
{

    @PersistenceContext
    private EntityManager entityManager;

    private CrudService serviceUnderTest;
//    private List<EntityServiceResultMatcher<E>> defaultEntityServiceResultMatchers;
//    private List<EntityCollectionServiceResultMatcher<E>> defaultEntityCollectionServiceResultMatchers;
//    private List<ServiceResultHandler> defaultServiceResultHandler;
    private ApplicationContext context;

    public void setServiceUnderTest(CrudService serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    public ServiceResultActions perform(ServiceRequestBuilder serviceRequestBuilder){
        ServiceRequest serviceRequest = serviceRequestBuilder.create(serviceUnderTest);
        serviceRequest.setService(serviceUnderTest);
        ServiceResult serviceResult = execute(serviceRequest);
        entityManager.flush();

        return new ServiceResultActions() {
            @Override
            public ServiceResultActions andExpect(ServiceResultMatcher matcher) {
                matcher.match(serviceResult,context);
                return this;
            }

            @Override
            public ServiceResultActions andDo(ServiceResultHandler handler) {
                handler.handle(serviceResult,context);
                return this;
            }

            @Override
            public ServiceResult andReturn() {
                return serviceResult;
            }
        };
    }

    private ServiceResult execute(ServiceRequest serviceRequest) {
        try {
            Object result = serviceRequest.getServiceMethod().invoke(serviceUnderTest,serviceRequest.getArgs().toArray());
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .result(result)
                    .build();
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .raisedException(e)
                    .build();
        }
    }


}
