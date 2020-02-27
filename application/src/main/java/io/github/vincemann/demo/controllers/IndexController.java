package io.github.vincemann.demo.controllers;

import io.github.vincemann.generic.crud.lib.config.WebComponent;
import org.springframework.web.bind.annotation.RequestMapping;

@WebComponent
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
