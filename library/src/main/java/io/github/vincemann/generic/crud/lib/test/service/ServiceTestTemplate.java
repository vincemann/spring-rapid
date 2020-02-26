package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequest;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequestBuilder;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityCollectionServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.action.EntityCollectionServiceResultActions;
import io.github.vincemann.generic.crud.lib.test.service.result.action.EntityServiceResultActions;
import io.github.vincemann.generic.crud.lib.test.service.result.action.ServiceResultActions;
import io.github.vincemann.generic.crud.lib.test.service.result.handler.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityCollectionServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityServiceResultMatcher;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;

@TestComponent
public class ServiceTestTemplate
    implements ApplicationContextAware
{

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
        ServiceResult<?> serviceResult = execute(serviceRequest);

        try {
            return new ServiceResultActions() {
                @Override
                public EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher){
                    if(matcher instanceof ApplicationContextAware){
                        ((ApplicationContextAware) matcher).setApplicationContext(context);
                    }
                    matcher.match(((EntityServiceResult) serviceResult));
                    return new EntityServiceResultActions() {
                        @Override
                        public EntityServiceResult andReturn() {
                            return (EntityServiceResult) serviceResult;
                        }

                        @Override
                        public EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher){
                            if(matcher instanceof ApplicationContextAware){
                                ((ApplicationContextAware) matcher).setApplicationContext(context);
                            }
                            matcher.match(((EntityServiceResult) serviceResult));
                            return this;
                        }
                    };
                }

                @Override
                public EntityCollectionServiceResultActions andExpect(EntityCollectionServiceResultMatcher matcher) {
                    if(matcher instanceof ApplicationContextAware){
                        ((ApplicationContextAware) matcher).setApplicationContext(context);
                    }
                    matcher.match(((EntityCollectionServiceResult) serviceResult));
                    return new EntityCollectionServiceResultActions() {
                        @Override
                        public EntityCollectionServiceResultActions andExpect(EntityCollectionServiceResultMatcher matcher)  {
                            if(matcher instanceof ApplicationContextAware){
                                ((ApplicationContextAware) matcher).setApplicationContext(context);
                            }
                            matcher.match(((EntityCollectionServiceResult) serviceResult));
                            return this;
                        }

                        @Override
                        public EntityCollectionServiceResult andReturn() {
                            return ((EntityCollectionServiceResult) serviceResult);
                        }
                    };
                }

                @Override
                public ServiceResultActions andDo(ServiceResultHandler handler)  {
                    return handler.handle(serviceResult);
                }

                @Override
                public ServiceResult andReturn() {
                    return serviceResult;
                }
            };
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Invalid matcher type",e);
        }
    }

    private ServiceResult execute(ServiceRequest serviceRequest) {
        try {
            Object result = serviceRequest.getServiceMethod().invoke(serviceRequest.getArgs().toArray());
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
