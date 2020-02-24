package io.github.vincemann.generic.crud.lib.test.service;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@DataJpaTest
public @interface ServiceIntegrationTest {
}
