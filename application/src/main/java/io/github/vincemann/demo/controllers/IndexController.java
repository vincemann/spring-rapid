package io.github.vincemann.demo.controllers;

import io.github.vincemann.generic.crud.lib.config.layers.component.ControllerComponent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerComponent
public class IndexController {

    @RequestMapping({"/","index","index.html"})
    public String index(){
        return "index";
    }


    @RequestMapping("/oups")
    public String oupsHandler(){
        return "notimplemented";
    }
}
