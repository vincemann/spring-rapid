# Overview  
This library aims to make the repetitive task, of implementing crud operations for all of your entities, become a cinch.  
This is done by providing highly extendable powerful generic classes, that are doing all the work, that comes with implementing crud.  
Also say goodbye to manually handling bidirectional relationships of entities. This is automatically done for all crud operations.  
But:  
This library is under active developement and the testcoverage is not sufficient yet.  
Feel free to extract the parts you need and/or help coding!  
  
  
# Features  
* JpaCrudService implementing crud on the service layer  
* proxybased PluginSystem for service  
* JsonCrudController implementing crud on the weblayer  
* Simple JavaX Validation  
* DtoMapping (support for different Dto's for each endpoint; diff request and response dto's)  
* uni- and bidirectional relationship management for all crud opeartions  
* automatic resolving of ids in dtos referencing other entities and vice versa  
* testsupport for service and controller layer (automocking of all service beans in controller tests)  
  
# Examples  

## What a typical fully functional crud controller looks like:  
  
```java
@Controller
public class ModuleController extends SpringAdapterJsonDtoCrudController<Module,Long> {

    public ModuleController() {
        //Diff Dto types are set here
        super(DtoMappingContext.WRITE_READ(CreateModuleDto.class, ReadModuleDto.class));
    }

    @Autowired
    @Override
    public void injectEndpointsExposureContext(EndpointsExposureContext ctx) {
        ctx.setUpdateEndpointExposed(false);
        ctx.setFindAllEndpointExposed(false);
        super.injectEndpointsExposureContext(ctx);
    }
}
```
  
  
## What a typical crud Service looks like:  
  
```java
@Service
@Transactional
@NoProxy
public class JpaModuleService
        extends JPACrudService<Module,Long,ModuleRepository>
                implements ModuleService {
}
```  
  
## What a typical entity looks like:  
  
```java
@Entity
@Table(name = "MODULE")
@EntityListeners(BiDirChildEntityListener.class)
public class Module extends DateAuditIdEntity<Long> implements UniDirParent, BiDirChild, BiDirParent {

    @NotEmpty
    private String name;

    @BiDirChildCollection(ExerciseGroup.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "module")
    private Set<ExerciseGroup> exerciseGroups = new HashSet<>();

    @UniDirChildEntity
    @OneToOne(fetch = FetchType.LAZY)
    private User creator;

    @BiDirParentEntity
    @ManyToOne
    private School school;
```  
  
# What a typical Dto looks like  
  
```java
public class ReadModuleDto extends AbstractModuleDto implements UniDirParentDto, BiDirParentDto {
    //creator
    @UniDirChildId(User.class)
    private Long creatorId;

    @BiDirChildIdCollection(ExerciseGroup.class)
    private Set<Long> exerciseGroupsIds = new HashSet<>();
}
```
  
  
# What a typical service config looks like:  
  
```java
@Configuration
public class ServiceConfig  {
    @Primary
    @Bean
    public ModuleService unsecuredModuleService(@NoProxy ModuleService moduleService,
                                                BiDirChildPlugin<Module, Long> biDirChildPlugin,
                                                //more Plugins can be added here...
    ) {
        return CrudServicePluginProxyFactory.create(moduleService,
                biDirChildPlugin
        );
    }
}
```  
  


