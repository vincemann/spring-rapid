package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequest;
import io.github.vincemann.generic.crud.lib.test.service.request.ServiceRequestBuilder;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResultActions;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class ServiceTestTemplate
    implements ApplicationContextAware
{

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
        ServiceTestContext testContext = ServiceTestContext.builder()
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
                matcher.match(testContext);
                return this;
            }

            @Override
            public ServiceResultActions andDo(ServiceResultHandler handler) {
                handler.handle(testContext);
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
                    AopTestUtils.getUltimateTargetObject(serviceUnderTest),
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
            return ServiceResult.builder()
                    .serviceRequest(serviceRequest)
                    .raisedException(((Exception) e.getTargetException()))
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
