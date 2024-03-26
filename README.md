# Overview  
Small Helper library for spring boot projects, provides abstractions for auth,acl and sync.  
  
Note:  
Inspired by [**spring-lemon**](https://github.com/naturalprogrammer/spring-lemon).   
Some modules were copied in modified version into this project.  
Includes some code from [**this repo**](https://github.com/dzinot/spring-boot-jpa-data-rest-soft-delete) to implement soft delete syncing.   
  
# Features                                                                 
* authentication (jwt)  
* acl  
* sync (with soft delete)  
  
# Requirements  
jdk: version 11.0.18 (default version for java 11)   
maven: use maven-wrapper ./mvnw  
spring-boot-starter-parent = 2.2.3.RELEASE   
  
# Build & Test    
Activate jdk 11.0.18. (tested on [amazon corretto](https://docs.aws.amazon.com/de_de/corretto/latest/corretto-11-ug/downloads-list.html))      
Use ``` ./build.sh ``` and ``` test*.sh ``` scripts.      
You can also run demo tests seperate via:      
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
  
# Example  
see demo projects for examples  


