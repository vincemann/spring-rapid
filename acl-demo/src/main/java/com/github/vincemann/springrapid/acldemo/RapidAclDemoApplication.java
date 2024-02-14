package com.github.vincemann.springrapid.acldemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class RapidAclDemoApplication
{

	public static void main(String[] args){
		SpringApplication.run(RapidAclDemoApplication.class, args);
	}

}
