package com.github.vincemann.springrapid.sync;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

public class SyncProperties {
    /**
     * configure base packages to be scanned, looking for dto classes annotated with {@link EntityMapping}
     */
    private List<String> basePackages = new ArrayList<>();

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

}
