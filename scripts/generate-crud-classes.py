import sys
import os
import logging
import re

logging.basicConfig(level=logging.INFO)


insert_entity_template = "<insert entity>"
insert_id_template = "<insert id>"
insert_package_template = "<insert package>"

# generates repo, service-interface, jpa-service-impl, controller for given entity
# and corresponding packages


# expects to be run in root classpath !
# in this example run in /path/to/project/src/main/java


# usage: python3 generate-crud-classes com.example MyEntity Long

cwd = os.path.realpath('.')

root_package = sys.argv[1]
entity_name = sys.argv[2]
id_type = sys.argv[3]

logging.info("root_package: %s " % root_package)
logging.info("entity_name: %s " % entity_name)
logging.info("id_type: %s " % id_type)


def to_snake_case(s):
	return re.sub(r'(?<!^)(?=[A-Z])', '_', s).lower()


def gen_dir_if_not_present(dir):
	logging.debug("creating dir: %s" % dir)
	if not os.path.exists(dir):
		os.makedirs(dir)


def adjust_template(template):
	# logging.info("entity_name in function: %s " % entity_name)
	# logging.info("insert_entity_template in function: %s " % insert_entity_template)
	template = template.replace(insert_entity_template,entity_name)
	template = template.replace(insert_id_template,id_type)
	template = template.replace(insert_package_template,root_package)
	return template


def write_template(dir,class_name,template):
	logging.info("writing template: ")
	logging.info(template)
	filename = dir + class_name + ".java"
	logging.info("final filename: %s " % filename)
	with open(filename, "w") as text_file:
		logging.info("generating class: %s in dir: %s", class_name, dir)
		text_file.write(template)


def gen_entity_class(dir):
	template = '''
package <insert package>.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "<insert table>")
public class <insert entity> extends IdentifiableEntityImpl<<insert id>> {

}
	'''
	template = adjust_template(template)
	template = template.replace("<insert table>",to_snake_case(entity_name))
	write_template(dir,entity_name,template)


def gen_repo_class(dir):
	template = '''
package <insert package>.repo;
import <insert package>.model.<insert entity>;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface <insert entity>Repository extends JpaRepository<<insert entity>,<insert id>> {

}
	'''
	template = adjust_template(template)
	write_template(dir,entity_name + "Repository",template)


def gen_service_class(dir):
	template = '''
package <insert package>.service;
import <insert package>.model.<insert entity>;
import com.github.vincemann.springrapid.core.service.CrudService;


public interface <insert entity>Service extends CrudService<<insert entity>,<insert id>> {
	
}
	'''
	template = adjust_template(template)
	write_template(dir,entity_name + "Service",template)


def gen_jpaservice_class(dir):
	template = '''
package <insert package>.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import <insert package>.model.<insert entity>;
import <insert package>.repo.<insert entity>Repository;
import <insert package>.service.<insert entity>Service;


@Service
@ServiceComponent
public class Jpa<insert entity>Service extends JPACrudService<<insert entity>,<insert id>, <insert entity>Repository> implements <insert entity>Service {
	
}
	'''
	template = adjust_template(template)
	write_template(dir,"Jpa" + entity_name + "Service",template)

def gen_controller_class(dir):
	template = '''
package <insert package>.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.slicing.WebController;
import <insert package>.model.<insert entity>;
import <insert package>.service.<insert entity>Service;

@WebController
public class <insert entity>Controller extends CrudController<<insert entity>, <insert id>, <insert entity>Service> {

	@Override
	protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
		// todo implement
		// return builder.forAll(<insert entity>Dto.class).build();
	}
}
	'''
	template = adjust_template(template)
	write_template(dir,entity_name + "Controller",template)


def gen_dir_name(start):
	return cwd + start.replace(".","/")


entity_dir_name = gen_dir_name("/%s/model/" % root_package)
repo_dir_name = gen_dir_name("/%s/repo/" % root_package)
controller_dir_name = gen_dir_name("/%s/controller/" % root_package)
service_dir_name = gen_dir_name("/%s/service/" % root_package)
jpaservice_dir_name = gen_dir_name("/%s/service/jpa/" % root_package)



logging.info("repo dir: %s" % repo_dir_name)
logging.info("entity dir: %s" % entity_dir_name)
logging.info("service dir: %s" % service_dir_name)
logging.info("jpaservice dir: %s" % jpaservice_dir_name)
logging.info("controller dir: %s" % controller_dir_name)

gen_dir_if_not_present(entity_dir_name)
gen_entity_class(entity_dir_name)

gen_dir_if_not_present(repo_dir_name)
gen_repo_class(repo_dir_name)

gen_dir_if_not_present(controller_dir_name)
gen_controller_class(controller_dir_name)

gen_dir_if_not_present(service_dir_name)
gen_service_class(service_dir_name)

gen_dir_if_not_present(jpaservice_dir_name)
gen_jpaservice_class(jpaservice_dir_name)

