package com.github.vincemann.springrapid.limitsaves;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ResetMaxAmountSavedEntitiesEventHandler {

    private List<LimitAmountSavedEntitiesExtension> rules;

    @Autowired
    public ResetMaxAmountSavedEntitiesEventHandler(List<LimitAmountSavedEntitiesExtension> rules) {
        this.rules = rules;
    }

    @EventListener(classes = {ResetAmountSavedEntitiesEvent.class})
    public void handleResetAmountCreatedModulesEvent(ResetAmountSavedEntitiesEvent resetAmountSavedEntitiesEvent){
        List<Class<?>> targetEntityClasses = resetAmountSavedEntitiesEvent.getTargetEntityClasses();
        log.debug(ResetAmountSavedEntitiesEvent.class +" coming in. Resetting limited amount for service of entityType: " + targetEntityClasses);
        AtomicBoolean reset = new AtomicBoolean(false);
        rules.forEach(rule -> {
            for (Class<?> targetEntityClass : targetEntityClasses) {
                if(rule.supports(targetEntityClass)){
                    rule.reset();
                    reset.set(true);
                }
            }
        });
        if(!reset.get()){
            log.warn("No Rule applied for ResetEvent");
        }
    }
}
