# Overview  
Some abstractions I use for spring developement for stuff like crud,auth,acl - structured into maven modules.    
Adds a common layer on top of spring that match most simple project requirements to allow for faster development.  
It's easiest to take a quick look at the demo projects.  
  
Note:  
Inspired by [**spring-lemon**](https://github.com/naturalprogrammer/spring-lemon) which offer many good modules.  
Most of them were copied, modified and integrated into this project.  
  
# Features  
* crud  
* bidirectional relationship management                                                                                                                          
* dto mapping with id resolving                                                                         
* acl             
* authentication (jwt)
  
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
    <version>1.0.0-SNAPSHOT.17</version>  
</dependency>  
```  
  
# crud example  
## Controller    
  
```java  
@Controller
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
  
                .forEndpoint(getCreateUrl(), CreateOwnerDto.class)  
                .forUpdate(UpdateOwnerDto.class)
                
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(ReadOwnOwnerDto.class)
  
                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
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
  


