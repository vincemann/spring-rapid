package io.github.vincemann.springrapid.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"io.github.vincemann.springrapid.demo",
		"io.github.vincemann.springrapid.core"})
@Slf4j
public class RapidDemoApplication {

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(RapidDemoApplication.class, args);
	}

}
