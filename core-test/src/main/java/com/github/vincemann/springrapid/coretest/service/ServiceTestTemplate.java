package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequest;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequestBuilder;
import com.github.vincemann.springrapid.coretest.service.result.*;
import com.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Use together with {@link CrudServiceIntegrationTest}.
 * Template similar to {@link org.springframework.test.web.servlet.MockMvc}, but build for testing of service layer ({@link CrudService}).
 * With this template you can build the test, that shall be executed against a {@link CrudService} in a fluent-API like manner.
 *
 * Creates a stateful {@link this#getTestContext()} that can be queried after test is {@link this#perform(ServiceRequestBuilder)}ed.
 * Other test-support framework-components make use of this context and can therefore only be used after a service test is performed.
 *
 */
@Slf4j
public class ServiceTestTemplate
{
    private EntityManager entityManager;
    private CrudService serviceUnderTest;
    private CrudRepository repository;
    private ApplicationContext applicationContext;
    @Getter
    private ServiceTestContext testContext;


    //    private List<EntityServiceResultMatcher<E>> defaultEntityServiceResultMatchers;
//    private List<EntityCollectionServiceResultMatcher<E>> defaultEntityCollectionServiceResultMatchers;
//    private List<ServiceResultHandler> defaultServiceResultHandler;

    @Builder(access = AccessLevel.PROTECTED)
    protected ServiceTestTemplate(EntityManager entityManager, CrudService serviceUnderTest, CrudRepository repository, ApplicationContext applicationContext) {
        this.entityManager = entityManager;
        this.serviceUnderTest = serviceUnderTest;
        this.repository = repository;
        this.applicationContext = applicationContext;
    }


    public ServiceResultActions perform(ServiceRequestBuilder serviceRequestBuilder){
        ServiceRequest serviceRequest = serviceRequestBuilder.create(serviceUnderTest);
        serviceRequest.setService(serviceUnderTest);
        ServiceResult serviceResult = execute(serviceRequest);
        testContext = ServiceTestContext.builder()
                .applicationContext(applicationContext)
                .repository(repository)
                .serviceRequest(serviceRequest)
                .serviceResult(serviceResult)
                .build();
        entityManager.flush();
        if (serviceResult.getRaisedException() != null) {
            log.warn("Service threw exception, this is wanted. Stacktrace: ");
            serviceResult.getRaisedException().printStackTrace();
        }

        return new ServiceResultActions() {

            @Override
            public ServiceResultActions andDo(ContextAwareResultHandler handler) {
                handler.handle(testContext);
                return this;
            }

            @Override
            public ServiceResultActions andDo(ServiceResultHandler handler) {
                handler.handle();
                return this;
            }

            @Override
            public ServiceResultActions andExpect(ContextAwareServiceResultMatcher matcher) {
                matcher.match(testContext);
                return this;
            }

            @Override
            public ServiceResultActions andExpect(ServiceResultMatcher matcher) {
                matcher.match();
                return this;
            }

            @Override
            public ServiceResult andReturn() {
                return serviceResult;
            }
        };
    }

    protected void reset(){
        this.testContext=null;
    }

    private ServiceResult execute(ServiceRequest serviceRequest) {
        try {
            Object result = serviceRequest.getServiceMethod().invoke(
//                    AopTestUtils.getUltimateTargetObject(serviceUnderTest),
                    serviceUnderTest,
                    serviceRequest.getArgs().toArray()
            );
            return ServiceResult.builder()
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
                    .raisedException(((Exception) cause))
                    .build();
        }
        catch (Exception e){
            if (serviceRequest.getExceptionWanted()){
                return ServiceResult.builder()
                        .raisedException(e)
                        .build();
            }else {
                throw e;
            }
        }
    }


    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected CrudService getServiceUnderTest() {
        return serviceUnderTest;
    }

    protected void setServiceUnderTest(CrudService serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    protected CrudRepository getRepository() {
        return repository;
    }

    protected void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
