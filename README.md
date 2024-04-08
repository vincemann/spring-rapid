# Overview  
Spring Boot helper lib for authentication and acl.  
This is an extended fork of [**spring-lemon**](https://github.com/naturalprogrammer/spring-lemon).  
The auth features were reduced to the (almost always needed) core and acl was added on top of it.   
  
# Features                                                                 
* auth (jwt)  
* acl  
* exception handling  
  
# Requirements  
* jdk:17  
* spring-boot: 3.1.1.RELEASE   
    
 # Include   
* replace MODULE with the module you want to include (acl,auth,auth-ex)  
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
    <version>1.0.0</version>  
</dependency>  
```  
  
# Example  
see demo projects for examples  


