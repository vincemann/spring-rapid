package io.github.vincemann.demo.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Slf4j
public class DataInitializerMock implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        //do nothing
        log.debug("mocked Data initializer called");
    }
}
