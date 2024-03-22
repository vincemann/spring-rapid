package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ContextController {

    private ContextService contextService;

    @GetMapping(path = "/api/core/context")
    public ResponseEntity<Map<String, Object>> context() throws JsonProcessingException {
        Map<String, Object> context = contextService.getContext();
        return ResponseEntity.ok(context);
    }

    @Autowired
    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
}
