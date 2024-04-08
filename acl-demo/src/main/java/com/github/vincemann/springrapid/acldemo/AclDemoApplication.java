package com.github.vincemann.springrapid.acldemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AclDemoApplication
{

	public static void main(String[] args){
		SpringApplication.run(AclDemoApplication.class, args);
	}

}
