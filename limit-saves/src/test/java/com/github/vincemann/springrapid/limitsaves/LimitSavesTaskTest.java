package com.github.vincemann.springrapid.limitsaves;


import com.github.vincemann.springrapid.core.config.RapidSecurityAutoConfiguration;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class LimitSavesTaskTest {


    private LimitSavesTask limitSavesTask;
    ApplicationEventPublisher mockedApplicationEventPublisher;

    @BeforeEach
    void setUp() {
         mockedApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        this.limitSavesTask = new LimitSavesTask();
    }

    // todo refactor tests
//    @Test
//    public void afterTimeLimitExceeded_shouldFireResetEvent(){
//        Date startDate = new Date();
//        limitSavesTask.setLastReset(startDate);
//        limitSavesTask.checkTimeLimits();
//        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
//        //now we wait until time limit is exceeded by faking last reset time to be in the past just far enough
//        int millisUntilTimeLimitExceeded = HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET * 60 * 60 * 1000+1;
//        Date timeLimitExceededDate = new Date(startDate.getTime()-millisUntilTimeLimitExceeded);
//        limitSavesTask.setLastReset(timeLimitExceededDate);
//        //now the time limit should be exceeded -> when spring calls scheduled task again, it should fire event
//        limitSavesTask.checkTimeLimits();
//        verify(mockedApplicationEventPublisher).publishEvent(isA(ResetSaveLimitEvent.class));
//        verify(mockedApplicationEventPublisher, atMostOnce()).publishEvent(any());
//    }
//
//    @Test
//    public void beforeTimeLimitExceeded_shouldNotFireResetEvent(){
//        Date startDate = new Date();
//        limitSavesTask.setLastReset(startDate);
//        limitSavesTask.checkTimeLimits();
//        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
//        //now we wait until time limit is almost exceeded by faking last reset time to be in the past
//        int millisUntilTimeLimitExceeded = HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET * 60 * 60 * 1000;
//        Date timeLimitAlmostExceededDate = new Date(startDate.getTime()-millisUntilTimeLimitExceeded+2000);
//        limitSavesTask.setLastReset(timeLimitAlmostExceededDate);
//        //now the time limit should be exceeded -> when spring calls scheduled task again, it should fire event
//        limitSavesTask.checkTimeLimits();
//        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
//    }



    // replace login user with mocking getPrincipal()

//    @SpringJUnitConfig(RapidSecurityAutoConfiguration.class)
//    class LimitAmountSavedEntitiesExtensionTest {
//
//
//        @Autowired
//        private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;
//
//        private TestLimitAmountSavedEntitiesExtension rule;
//        private RapidAuthenticatedPrincipal testUser1 = TestPrincipal.withName("meier");
//        private RapidAuthenticatedPrincipal testUser2 = TestPrincipal.withName("müller");
//        private static final
//
//        @BeforeEach
//        void setUp() {
//            rule = new TestLimitAmountSavedEntitiesExtension();
//        }
//
//        @AllArgsConstructor
//        @Getter
//        @Setter
//        static class TestEntity extends IdentifiableEntityImpl<Long> {
//            private String name;
//        }
//
//        class TestLimitAmountSavedEntitiesExtension extends LimitSavesExtension {
//            public TestLimitAmountSavedEntitiesExtension() {
//                super(2,);
//            }
//        }
//
//        @Test
//        public void saveEntityMoreOftenThenAllowed_shouldThrowException() throws BadEntityException {
//            securityContext.login(testUser1);
//            for (int i = 0; i < rule.getMaxAmountSavedEntities(); i++) {
//                rule.newEntityCreated();
//            }
//            Assertions.assertThrows(TooManyRequestsException.class,()->rule.checkAmountEntitiesCreated());
//        }
//
//        @Test
//        public void saveTooOften_reset_shouldAllowSavingAgain(){
//            securityContext.login(testUser1);
//            for (int i = 0; i < rule.getMaxAmountSavedEntities(); i++) {
//                rule.newEntityCreated();
//            }
//            Assertions.assertThrows(TooManyRequestsException.class,()->rule.checkAmountEntitiesCreated());
//
//            rule.reset();
//            rule.checkAmountEntitiesCreated();
//        }
//
//        @Test
//        public void saveTooOftenUser1_user2NotAffected(){
//            securityContext.login(testUser1);
//            for (int i = 0; i < rule.getMaxAmountSavedEntities(); i++) {
//                rule.newEntityCreated();
//            }
//            Assertions.assertThrows(TooManyRequestsException.class,()->rule.checkAmountEntitiesCreated());
//            securityContext.logout();
//            securityContext.login(testUser2);
//            rule.checkAmountEntitiesCreated();
//        }
//
//        @AfterEach
//        void tearDown() {
//            securityContext.logout();
//        }
//    }
}