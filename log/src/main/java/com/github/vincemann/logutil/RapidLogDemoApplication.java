package com.github.vincemann.logutil;

import com.github.vincemann.springrapid.autobidir.DisableAutoBiDir;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class RapidLogDemoApplication {

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(RapidLogDemoApplication.class, args);
	}

}
