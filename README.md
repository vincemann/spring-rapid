# Overview  
!! Under Active Developement !!  
Spring Rapid consists of modules encapsulating solutions for **common issues** when developing Spring REST APIs.  
The goal is too **speed up** the developement process and stop rewriting the same code over and over again.  
Instead there should be very well implemented modules for each task, that can be plugged in whenever needed.  
One common task, that is very repetitive, is implementing **Crud-Operations** for all of your entities.  
This library gives you a **fully functional** and autoconfigured (yet hightly configurable/ extendable) solution for implementing crud,  
that only consists of **a few lines of code**.  
The code needed for a working Controller handling multiple different Dto Types can be seen in the example section below.  
Along with the Crud Module (Core) come many other modules building upon the core module, that can be plugged in when needed.  
  
This project is inspired by **Spring-Lemon** which offers many good modules as well.  
Any help with developement is greatly appreciated.  
Feel free to use, download and modify any code you want.  
  
  
# Features  
* Generic Crud Implementation for Controller and Servicelayer                                        (Core)  
* Proxybased PluginSystem for Service                                                                (Core)  
* Basic Exception to ApiError translation                                                            (Exceptions)  
* Dto-Mapping (support for different Dto's for each endpoint; diff request and response dto's)       (Core)  
* Uni- and Bidirectional relationship management for all crud opeartions                             (Entity-Relationship)  
* Automatic resolving of ids in dtos referencing other entities and vice versa                       (Entity-Relationship)  
* Testsupport for service and controller layer                                                       (Core-Test)  
* Out of the box working Acl Module with SecurityProxy solution for applying Acl-based, pluginlike 
  rules that restrict access to service methods                                                       (Acl)            
    
    
# Example  
**This is the typical setup required to run a fully functional controller exposing crud enpoints for one entity:**  
**Check out the rapid-demo module for a more complete example!**  
## Controller    
  
```java
@WebController
public class ModuleController extends RapidController<Module,Long> {

    public ModuleController() {
        super(DtoMappingContextBuilder.builder()
                .forResponse(ReadModuleDto.class)
                .forEndpoint(CrudDtoEndpoint.CREATE, Direction.REQUEST, CreateModuleDto.class)
                .build()
        );
    }
```
  
  
## Service   
  
```java
@Service
@NoProxy
public class JpaModuleService
        extends JPACrudService<Module,Long,ModuleRepository>
                implements ModuleService {
}
```  
  
## Entity    
  
```java
@Entity
public class Module extends IdentifiableEntityImpl<Long> implements UniDirParent, BiDirChild, BiDirParent {

    private String name;

    //both sides of BiDir- Relationships are automatically handled by the Framework  
    @BiDirChildCollection(ExerciseGroup.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "module")
    private Set<ExerciseGroup> exerciseGroups = new HashSet<>();

    //UniDir Relationships are also marked with Annotations for automatic Dto-Mapping (Entity gets resolved from id)  
    @UniDirChildEntity
    @OneToOne(fetch = FetchType.LAZY)
    private User creator;

    @BiDirParentEntity
    @ManyToOne
    private School school;
```  
  
## Dto  
  
```java
public class ReadModuleDto extends AbstractModuleDto implements UniDirParentDto, BiDirParentDto {

    //meta information used to automatically resolve creator by id when mapping dto to entity
    @UniDirChildId(User.class)
    private Long creatorId;

    @BiDirChildIdCollection(ExerciseGroup.class)
    private Set<Long> exerciseGroupsIds = new HashSet<>();
}
```
  
## Service Config    
  
```java
@ServiceConfig
public class ModuleServiceConfig  {
    //define multiple service proxy beans here, i.E. :
    
    //can be wired in with @Autowired @AclManaging if you want to use the version of the service, that also stores acl information  
    @AclManaging
    @Bean
    public ModuleService aclModuleService(      ModuleService moduleService,
                                                YourAclPlugin aclPlugin,
                                                //more Plugins can be added here...
                                                ) {
        return CrudServicePluginProxyFactory.create(moduleService,aclPlugin);
    }
    
    @Primary
    @Bean
    public ModuleService normalModuleService(@NoProxy ModuleService moduleService,
                                                      LogCreationPlugin logPlugin,
                                                      //more Plugins can be added here...
                                                      ) {
        return CrudServicePluginProxyFactory.create(moduleService,logPlugin);
    }
}
```  
  
## Installation  
See 'Packages' section in this repo for maven dependencies tag and artifacts.  


