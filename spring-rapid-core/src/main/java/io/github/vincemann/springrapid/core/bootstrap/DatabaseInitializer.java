package io.github.vincemann.springrapid.core.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//test executes this via TestExecutionListener
public class DatabaseInitializer implements CommandLineRunner {

    private List<DatabaseDataInitializer> databaseDataInitializers = new ArrayList<>();

    @Autowired(required = false)
    public void injectDatabaseDataInitializers(List<DatabaseDataInitializer> databaseDataInitializers) {
        this.databaseDataInitializers = databaseDataInitializers;
    }

    @Override
    public void run(String... args) throws Exception {
        for (DatabaseDataInitializer databaseDataInitializer : databaseDataInitializers) {
            databaseDataInitializer.loadInitData();
        }
    }
}
