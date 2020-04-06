# Overview  
This library aims to make the repetitive task, of implementing crud operations for all of your entities, become a cinch.  
This is done by providing highly extendable powerful generic classes, that are doing all the work, that comes with implementing crud.  
Also say goodbye to manually handling bidirectional relationships of entities. This is automatically done for all crud operations.  
But:  
This library is under active developement and the testcoverage is not sufficient yet.  
Feel free to extract the parts you need and/or help coding!  
See the demo application under rapid-demo/ and the code examples below to get a feeling for what it is all about.  
  
  
# Features  
* JpaCrudService implementing crud on the service layer  
* Proxybased PluginSystem for service  
* JsonCrudController implementing crud on the weblayer  
* Basic Exception to ApiError translation  
* DtoMapping (support for different Dto's for each endpoint; diff request and response dto's)  
* Uni- and Bidirectional relationship management for all crud opeartions  
* Automatic resolving of ids in dtos referencing other entities and vice versa  
* Testsupport for service and controller layer (automocking of all service beans in controller tests)  
  
# Example  
**This is the setup required to run a fully functional controller exposing crud enpoints for one entity:**  
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
  



