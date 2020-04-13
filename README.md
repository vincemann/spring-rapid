# Overview  
Spring Rapid consists of modules encapsulating solutions for common issues when developing Spring Rest Apis.  
The goal is too speed up the developement process and stop rewriting the same code over and over again.  
Instead there should be very well implemented modules for each task, that can be plugged in whenever needed.  
One common task, that is very repetitive, is implementing Cruc-Operations for all of your entities.  
This library gives you a fully functional and autoconfigured (yet hightly configurable/ extendable) solution for implementing crud,  
that only consists of a few lines of code.  
The lines of code needed for a working Controller handling multiple different Dto Types can be seen in the example section below.  
Along with the Crud Module (Core) come many other modules building upon the core module, that can be plugged in when needed.  
  
  
# Features  
* Generic Crud Implementation for Controller and Servicelayer                                        (Core)  
* Proxybased PluginSystem for Service                                                                (Core)  
* Basic Exception to ApiError translation                                                            (Exceptions)  
* Dto-Mapping (support for different Dto's for each endpoint; diff request and response dto's)       (Core)  
* Uni- and Bidirectional relationship management for all crud opeartions                             (EntityRelationShip)  
* Automatic resolving of ids in dtos referencing other entities and vice versa                       (EntityRelationShip)  
* Testsupport for service and controller layer                                                       (Core-Test)  
* Out of the box working Acl Module with SecurityProxy solution for applying Acl-based, pluginlike 
  rules that restrict access to service methods                                                       (Acl)            
    
    
# Example  
**This is the typical setup required to run a fully functional controller exposing crud enpoints for one entity:**  
## Controller    
  
```java
@Controller
public class ModuleController extends SpringAdapterJsonDtoCrudController<Module,Long> {

    public ModuleController() {
        //Diff Dto types are set here
        super(DtoMappingContext.WRITE_READ(CreateModuleDto.class, ReadModuleDto.class));
    }
}
```
  
  
## Service   
  
```java
@Service
@Transactional
@NoProxy
public class JpaModuleService
        extends JPACrudService<Module,Long,ModuleRepository>
                implements ModuleService {
}
```  
  
## Entity    
  
```java
@Entity
@Table(name = "MODULE")
public class Module extends DateAuditIdEntity<Long> implements UniDirParent, BiDirChild, BiDirParent {

    @NotEmpty
    private String name;

    //both sides of BiDir- Relationships indicated by these Annotations are automatically handled by the Framework  
    @BiDirChildCollection(ExerciseGroup.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "module")
    private Set<ExerciseGroup> exerciseGroups = new HashSet<>();

    //UniDir Relationships are also marked with Annotations for automatic Dto-Mapping (Entity gets resolved to id)  
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

    @UniDirChildId(User.class)
    private Long creatorId;

    @BiDirChildIdCollection(ExerciseGroup.class)
    private Set<Long> exerciseGroupsIds = new HashSet<>();
}
```
  
  
## Service Config    
  
```java
//the framework is divided in Service and WebConfigs, so when you are testing the ServiceLayer for example, all //WebConfigs and Components wont be loaded (diff ApplicationContext)  
@ServiceConfig
public class ModuleServiceConfig  {
    //define multiple proxied service beans here, i.E. :
    
    
    @AclManaging
    @Bean
    public ModuleService aclModuleService(      ModuleService moduleService,
                                                YourAclPlugin aclPlugin,
                                                //more Plugins can be added here...
    ) {
        return CrudServicePluginProxyFactory.create(moduleService,
                aclPlugin
        );
    }
    

    @Primary
    @Bean
    public ModuleService normalModuleService(@NoProxy ModuleService moduleService,
                                                      LogCreationPlugin logPlugin,
                                                      //more Plugins can be added here...
    ) {
        return CrudServicePluginProxyFactory.create(moduleService,
                logPlugin
        );
    }
}
```  
  



