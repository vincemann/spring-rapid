package com.github.vincemann.springrapid.limitsaves;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
//@Profile({Profiles.DEV,Profiles.PROD})
@Setter
public class LimitSavesTask {


    private List<LimitSavesExtension> extensions;
    private Map<LimitSavesExtension,Date> extension_lastReset_map = new HashMap<>();


    @EventListener(classes = {ContextStartedEvent.class})
    public void init(){
        new ResetAllSaveLimitsEvent();
    }

    //default : execute every minute
    @Scheduled(fixedDelayString = "${rapid-limit-saves.checkInterval:60000}")
    public void checkTimeLimits(){
        long now = new Date().getTime();
        for (LimitSavesExtension extension : extensions) {
            Date lastReset = extension_lastReset_map.get(extension);
            if (now-lastReset.getTime()>extension.getTimeInterval()){
                log.debug("Resetting saves-limit for service managing entities of type: " + extension.getEntityClass());
                extension.reset();
                extension_lastReset_map.put(extension,new Date());
            }
        }
    }

    @Autowired
    public void injectExtensions(List<LimitSavesExtension> extensions) {
        this.extensions = extensions;
        for (LimitSavesExtension extension : extensions) {
            extension_lastReset_map.put(extension,new Date());
        }
    }
}
