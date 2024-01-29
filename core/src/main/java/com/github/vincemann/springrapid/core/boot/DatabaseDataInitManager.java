package com.github.vincemann.springrapid.core.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

//test executes this via TestExecutionListener
public class DatabaseDataInitManager implements CommandLineRunner {

    private List<DatabaseInitializer> initializers = new ArrayList<>();

    @Autowired(required = false)
    public void injectInitializers(List<DatabaseInitializer> initializers) {
        this.initializers = initializers;
    }

    @Override
    public void run(String... args) throws Exception {
        for (DatabaseInitializer initializer : initializers) {
            initializer.init();
        }
    }
}
