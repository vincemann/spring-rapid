package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.core.slicing.components.WebController;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
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
