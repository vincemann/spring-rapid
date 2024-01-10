package com.github.vincemann.springrapid.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

@Getter
@Setter
public class CoreProperties {

    public CoreProperties() {
    }

    public Controller controller = new Controller();
    public String baseUrl = "/api/core";
    /**
     * Client web application's base URL.
     * Used in the verification link mailed to the users, etc.
     */
    public String applicationUrl = "http://localhost:9000";



    @Getter
    @Setter
    public static class Controller{

        // if set to true, BadEntityException will be thrown if user tries to update unsupported field
        // if set to false, warning is logged and unsupported action is ignored
        public boolean strictUpdateMerge = true;
        // dont change to something other than json, for now only json is supported
        public String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        public Endpoints endpoints = new Endpoints();

        @Getter
        @Setter
        public static class Endpoints{
            // just the names of the method in url, since url is dynamically build
            public String update = "update";
            public String create = "create";
            public String find = "find";
            public String findAll = "find-all";
            public String findSome = "find-some";
            public String delete = "delete";
            public String findAllOfParent = "find-all-of-parent";
        }
    }





}
