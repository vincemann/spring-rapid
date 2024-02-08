package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.service.ctx.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
