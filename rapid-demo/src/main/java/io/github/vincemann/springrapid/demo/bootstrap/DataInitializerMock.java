package io.github.vincemann.springrapid.demo.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@TestComponent
@Slf4j
public class DataInitializerMock implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        //do nothing
        log.debug("mocked Data initializer called");
    }
}
