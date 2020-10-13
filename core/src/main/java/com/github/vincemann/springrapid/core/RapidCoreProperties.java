package com.github.vincemann.springrapid.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

@Getter
@Setter
public class RapidCoreProperties {

    public RapidCoreProperties() {
    }

    public Controller controller = new Controller();
    public String baseUrl = "/api/core";

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

            public String update = "update";
            public String create = "create";
            public String find = "find";
            public String findAll = "findAll";
            public String delete = "delete";
            public String findAllOfParent = "findAllOfParent";
        }
    }





}
