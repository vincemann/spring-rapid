# Overview  
Spring-rapid consists of modules encapsulating solutions for **common issues** when developing spring **REST APIs**.  
One common task that is very repetitive is implementing **crud-operations** for all of your entities.    
The core module gives you a solution for that requirering only **a few lines of code**.  
The code needed for a working controller, handling multiple different dto-types, can be seen in the example below.  
There are many other modules building upon the core module that can be plugged in when needed.  
See 'Features' below for a full list.  
  
This project is inspired by [**spring-lemon**](https://github.com/naturalprogrammer/spring-lemon) which offers many good modules as well.  
Most lemon modules were copied, modified and integrated into this project.  
  
# Requirements  
jdk: version 11.0.18 (default version for java 11)   
maven: use maven-wrapper ./mvnw  
spring-boot-starter-parent = 2.2.3.RELEASE   
  
# Build & Test    
activate jdk 11.0.18 (tested on [amazon corretto](https://docs.aws.amazon.com/de_de/corretto/latest/corretto-11-ug/downloads-list.html))      
use ``` ./build.sh ``` and ``` test*.sh ``` scripts.    
they use mvnw and make sure auth-demo test's wont fail for no reason      
you can also run auth-demo's tests seperate via:      
``` cd ./auth-demo/; ./test.sh ```   
# Features  
* full generic **crud** solution                                                                     (core)   
* proxy-based **extension system** for services                                                      (core)  
  -> create different kind of service beans (i.E. @Secured, @Acl) that use reusable extensions  
* basic exception to api-error translation                                                           (lemon-exceptions)  
* **dto-mapping** (support for different dto's for each endpoint; see example below)                 (core)  
* uni- and bidirectional relationship management for all crud operations                             (auto-bidir)  
* resolving of ids <-> entities, while mapping dto's                                                 (auto-bidir)  
* test-support for service- and controller-tests                                                     (core-test)  
* automatic **acl**-scheme setup + simple API for writing acl-based service-extensions               (acl)  
* full solution for jwt based **user authentication**                                                (auth)  
  -> signup, login, reset-password, verify-email, ...
    
 # Include   
* replace MODULE with the module you want to include (module-name = directory-name)  
* versions can be seen here: [releases](https://github.com/vincemann/spring-rapid/releases)  
 ### Maven  
 ```code  
<repositories>    
    <repository>   
        <id>jitpack.io</id>  
        <url>https://jitpack.io</url>  
    </repository>  
</repositories>  
  
<dependency>  
    <groupId>com.github.vincemann.spring-rapid</groupId>  
    <artifactId>MODULE</artifactId>  
    <version>1.0.0-SNAPSHOT.16</version>  
</dependency>  
```  
  
# Example  
**This is the typical setup required to run a fully functional controller exposing crud enpoints for one entity.**  
**Check out the demo module for a more complete example + tests.**  
## Controller    
  
```java  
@Controller
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
  
                .withAllPrincipals()  
                .forEndpoint(getCreateUrl(), CreateOwnerDto.class)  
  
                .withAllPrincipals()  
                .forUpdate(UpdateOwnerDto.class)
                
                
                //response dto config for retrieving own Owner dto  
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(ReadOwnOwnerDto.class)
  
                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
                .forResponse(ReadForeignOwnerDto.class)
  
                .withAllPrincipals()
                .forResponse(ReadForeignOwnerDto.class)
                .build();
    }

}

```
  
  
## Service   
  
```java  
@Service  
public class JpaOwnerService  
        extends JPACrudService<Owner,Long,OwnerRepository>  
                implements OwnerService {  
}  

```  
 
  


