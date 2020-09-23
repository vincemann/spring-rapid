package com.github.vincemann.springrapid.core.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

//test executes this via TestExecutionListener
public class DatabaseDataInitManager implements CommandLineRunner {

    private List<DatabaseDataInitializer> initializers = new ArrayList<>();

    //todo not required?
    @Autowired(required = false)
    public void injectInitializers(List<DatabaseDataInitializer> initializers) {
        this.initializers = initializers;
    }

    @Override
    public void run(String... args) throws Exception {
        for (DatabaseDataInitializer initializer : initializers) {
            initializer.init();
        }
    }
}
