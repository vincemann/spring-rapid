package com.github.vincemann.springrapid.core;

import java.util.Map;

public class CoreProperties {

    public CoreProperties() {
    }

    public Controller controller = new Controller();
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

    public void setController(Controller controller) {
        this.controller = controller;
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

    public static class Controller{

        // if set to true, BadEntityException will be thrown if user tries to update unsupported field
        // if set to false, warning is logged and unsupported action is ignored
        public boolean strictUpdateMerge = true;
        public Endpoints endpoints = new Endpoints();

        public static class Endpoints{
            // just the names of the method in url, since url is dynamically build
            public String update = "update";
            public String create = "create";
            public String find = "find";
            public String findAll = "find-all";
            public String findSome = "find-some";
            public String delete = "delete";
            public String findAllOfParent = "find-all-of-parent";

            public String getUpdate() {
                return update;
            }

            public void setUpdate(String update) {
                this.update = update;
            }

            public String getCreate() {
                return create;
            }

            public void setCreate(String create) {
                this.create = create;
            }

            public String getFind() {
                return find;
            }

            public void setFind(String find) {
                this.find = find;
            }

            public String getFindAll() {
                return findAll;
            }

            public void setFindAll(String findAll) {
                this.findAll = findAll;
            }

            public String getFindSome() {
                return findSome;
            }

            public void setFindSome(String findSome) {
                this.findSome = findSome;
            }

            public String getDelete() {
                return delete;
            }

            public void setDelete(String delete) {
                this.delete = delete;
            }

            public String getFindAllOfParent() {
                return findAllOfParent;
            }

            public void setFindAllOfParent(String findAllOfParent) {
                this.findAllOfParent = findAllOfParent;
            }
        }

        public boolean isStrictUpdateMerge() {
            return strictUpdateMerge;
        }

        public void setStrictUpdateMerge(boolean strictUpdateMerge) {
            this.strictUpdateMerge = strictUpdateMerge;
        }

        public Endpoints getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(Endpoints endpoints) {
            this.endpoints = endpoints;
        }
    }





}
