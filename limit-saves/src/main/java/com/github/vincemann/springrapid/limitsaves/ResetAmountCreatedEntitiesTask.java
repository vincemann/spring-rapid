package com.github.vincemann.springrapid.limitsaves;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
//@Profile({Profiles.DEV,Profiles.PROD})
@Setter
public class ResetAmountCreatedEntitiesTask {

    public static final Integer HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET = 4;

    private ApplicationEventPublisher applicationEventPublisher;
    private Date lastReset = new Date();

    public ResetAmountCreatedEntitiesTask(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener(classes = {ContextStartedEvent.class})
    public void init(){
        new ResetAmountSavedEntitiesEvent(this, Module.class,ExerciseGroup.class);
    }

    //execute every 5 minutes
    @Scheduled(fixedRate = 5*60*1000)
    public void checkTimeLimits(){
        long deltaMillis = new Date().getTime() - lastReset.getTime();

        if(deltaMillis> HOURS_UNTIL_AMOUNT_ENTITIES_CREATED_RESET *60*60*1000){
            log.debug("ResetAmountCreatedModulesEvent triggered");
            applicationEventPublisher.publishEvent(new ResetAmountSavedEntitiesEvent(this, Module.class,ExerciseGroup.class));
            lastReset = new Date();
        }
    }
}
