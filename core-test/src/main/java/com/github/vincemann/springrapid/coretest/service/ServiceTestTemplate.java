package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequest;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequestBuilder;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResult;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResultActions;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResultHandler;
import com.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import com.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Template similar to {@link org.springframework.test.web.servlet.MockMvc}, but build for testing of service layer ({@link CrudService}).
 * With this template you can build the test, that shall be executed against a {@link CrudService} in a fluent-API like manner.
 */
@Slf4j
public class ServiceTestTemplate
    implements ApplicationContextAware
{

    private static ServiceTestContext TEST_CONTEXT;

    /**
     * Use to get latest TestContext instance.
     * Dont call before Test is initiated by {@link this#perform(ServiceRequestBuilder)}
     */
    public static ServiceTestContext getTestContext(){
        if (TEST_CONTEXT== null){
            throw new IllegalStateException("Text Context not initialized.");
        }
        return TEST_CONTEXT;
    }


    private EntityManager entityManager;
    private CrudService serviceUnderTest;
    private CrudRepository repository;
//    private List<EntityServiceResultMatcher<E>> defaultEntityServiceResultMatchers;
//    private List<EntityCollectionServiceResultMatcher<E>> defaultEntityCollectionServiceResultMatchers;
//    private List<ServiceResultHandler> defaultServiceResultHandler;
    private ApplicationContext applicationContext;

    public void setServiceUnderTest(CrudService serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext =applicationContext;
    }


    public ServiceResultActions perform(ServiceRequestBuilder serviceRequestBuilder){
        ServiceRequest serviceRequest = serviceRequestBuilder.create(serviceUnderTest);
        serviceRequest.setService(serviceUnderTest);
        ServiceResult serviceResult = execute(serviceRequest);
        TEST_CONTEXT = ServiceTestContext.builder()
                .applicationContext(applicationContext)
                .repository(repository)
                .serviceResult(serviceResult)
                .build();
        entityManager.flush();
        if (serviceResult.getRaisedException() != null) {
            log.warn("Service threw exception, this might have been wanted. Stacktrace: ");
            serviceResult.getRaisedException().printStackTrace();
        }

        return new ServiceResultActions() {
            @Override
            public ServiceResultActions andExpect(ServiceResultMatcher matcher) {
                matcher.match();
                return this;
            }

            @Override
            public ServiceResultActions andDo(ServiceResultHandler handler) {
                handler.handle();
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
            Object result = serviceRequest.getServiceMethod().invoke(
                    //todo ist das hier nicht bs? warum sollte ich die aspekt proxys deactivieren?
                    AopTestUtils.getUltimateTargetObject(serviceUnderTest),
                    //serviceUnderTest,
                    serviceRequest.getArgs().toArray()
            );
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .result(result)
                    .build();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e){
            Throwable cause = e;
            do {
                cause = cause.getCause();
            }while (cause instanceof InvocationTargetException);
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .raisedException(((Exception) cause))
                    .build();
        }
        catch (Exception e){
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .raisedException(e)
                    .build();
        }
    }


}
