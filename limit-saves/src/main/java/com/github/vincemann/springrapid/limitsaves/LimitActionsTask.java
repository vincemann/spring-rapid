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
@Setter
/**
 * Start this task, and thus the whole limit actions engine, by including this task in the application context by exposing it
 * with @Bean in a @Configuration class.
 *
 *
 * i.E.
 * @Configuration
 * @Profile({Profiles.DEV,Profiles.PROD})
 * @EnableScheduling
 * public class ScheduledConfig {
 *
 *     @Bean
 *     public LimitActionsTask limitActionsTask(){
 *         return new LimitActionsTask();
 *     }
 * }
 *
 */
public class LimitActionsTask {


    private List<LimitActionsExtension> extensions;
    private Map<LimitActionsExtension, Date> extension_lastReset_map = new HashMap<>();


    @EventListener(classes = {ContextStartedEvent.class})
    public void init() {
        log.debug("Context refreshed -> resetting all limit-save clocks");
        resetAll();
    }

    //default : execute every minute
    @Scheduled(fixedDelayString = "${rapid-limit-saves.checkInterval:60000}")
    public void checkTimeLimits() {
        long now = new Date().getTime();
        for (LimitActionsExtension extension : extensions) {
            Date lastReset = extension_lastReset_map.get(extension);
            if (now - lastReset.getTime() > extension.getTimeInterval()) {
                log.debug("Resetting saves-limit for service managing entities of type: " + extension.getEntityClass());
                extension.reset();
                extension_lastReset_map.put(extension, new Date());
            }
        }
    }

    public void resetAll() {
        for (LimitActionsExtension extension : extensions) {
            extension.reset();
            extension_lastReset_map.put(extension, new Date());
        }
    }

    @Autowired
    public void injectExtensions(List<LimitActionsExtension> extensions) {
        this.extensions = extensions;
        for (LimitActionsExtension extension : extensions) {
            extension_lastReset_map.put(extension, new Date());
        }
    }
}
