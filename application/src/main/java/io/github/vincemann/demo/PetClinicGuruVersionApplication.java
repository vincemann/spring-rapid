package io.github.vincemann.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@ComponentScan(basePackages = {
		"io.github.vincemann.demo",
		"io.github.vincemann.generic.crud.lib"})
@Slf4j
public class PetClinicGuruVersionApplication {

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(PetClinicGuruVersionApplication.class, args);
		for (String beanDefinitionName : context.getBeanDefinitionNames()) {
			log.debug("beandefname: " + beanDefinitionName);
		}
	}

}
