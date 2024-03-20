package com.github.vincemann.springrapid.core;

import java.util.Map;

public class CoreProperties {

    public CoreProperties() {
    }

    public String baseUrl = "/api/core";
    public String contextUrl = baseUrl + "/context";

    /**
     * Client web application's base URL.
     * Used in the verification link mailed to the users, etc.
     */
    public String applicationUrl = "http://localhost:9000";


    public Map<String, Object> shared;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getContextUrl() {
        return contextUrl;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public Map<String, Object> getShared() {
        return shared;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setContextUrl(String contextUrl) {
        this.contextUrl = contextUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public void setShared(Map<String, Object> shared) {
        this.shared = shared;
    }


}
