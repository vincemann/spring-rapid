package com.github.vincemann.springrapid.core.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

//test executes this via TestExecutionListener
public class DatabaseInitManager implements CommandLineRunner {

    private List<Initializer> initializers = new ArrayList<>();

    //todo not required?
    @Autowired(required = false)
    public void injectInitializers(List<Initializer> initializers) {
        this.initializers = initializers;
    }

    @Override
    public void run(String... args) throws Exception {
        for (Initializer initializer : initializers) {
            initializer.init();
        }
    }
}
