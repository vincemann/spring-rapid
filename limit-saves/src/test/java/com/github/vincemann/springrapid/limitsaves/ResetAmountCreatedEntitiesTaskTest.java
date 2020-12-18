package com.github.vincemann.springrapid.limitsaves;

import io.gitlab.vinceconrad.votesnackbackend.event.ResetAmountSavedEntitiesEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;

import static io.gitlab.vinceconrad.votesnackbackend.rapid.scheduled.ResetAmountCreatedEntitiesTask.HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class ResetAmountCreatedEntitiesTaskTest {


    private ResetAmountCreatedEntitiesTask resetAmountCreatedEntitiesTask;
    ApplicationEventPublisher mockedApplicationEventPublisher;

    @BeforeEach
    void setUp() {
         mockedApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        this.resetAmountCreatedEntitiesTask = new ResetAmountCreatedEntitiesTask(mockedApplicationEventPublisher);
    }

    @Test
    public void afterTimeLimitExceeded_shouldFireResetEvent(){
        Date startDate = new Date();
        resetAmountCreatedEntitiesTask.setLastReset(startDate);
        resetAmountCreatedEntitiesTask.checkTimeLimits();
        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
        //now we wait until time limit is exceeded by faking last reset time to be in the past just far enough
        int millisUntilTimeLimitExceeded = HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET * 60 * 60 * 1000+1;
        Date timeLimitExceededDate = new Date(startDate.getTime()-millisUntilTimeLimitExceeded);
        resetAmountCreatedEntitiesTask.setLastReset(timeLimitExceededDate);
        //now the time limit should be exceeded -> when spring calls scheduled task again, it should fire event
        resetAmountCreatedEntitiesTask.checkTimeLimits();
        verify(mockedApplicationEventPublisher).publishEvent(isA(ResetAmountSavedEntitiesEvent.class));
        verify(mockedApplicationEventPublisher, atMostOnce()).publishEvent(any());
    }

    @Test
    public void beforeTimeLimitExceeded_shouldNotFireResetEvent(){
        Date startDate = new Date();
        resetAmountCreatedEntitiesTask.setLastReset(startDate);
        resetAmountCreatedEntitiesTask.checkTimeLimits();
        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
        //now we wait until time limit is almost exceeded by faking last reset time to be in the past
        int millisUntilTimeLimitExceeded = HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET * 60 * 60 * 1000;
        Date timeLimitAlmostExceededDate = new Date(startDate.getTime()-millisUntilTimeLimitExceeded+2000);
        resetAmountCreatedEntitiesTask.setLastReset(timeLimitAlmostExceededDate);
        //now the time limit should be exceeded -> when spring calls scheduled task again, it should fire event
        resetAmountCreatedEntitiesTask.checkTimeLimits();
        verify(mockedApplicationEventPublisher, never()).publishEvent(any());
    }
}