package com.github.vincemann.springrapid.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class RapidDemoApplication {

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(RapidDemoApplication.class, args);
	}

}
