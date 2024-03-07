package com.github.vincemann.springrapid.autobidir.controller.unidir;


//@Getter
//public class UniDirParentResolverTest  {
//
//    private UniDirParentIdResolver uniDirParentIdResolver;
//
//    @Mock
//    private CrudServiceLocator crudServiceLocator;
//    @Mock
//    private CrudService<UniDirEntityChild,Long> entityChildCrudService;
//    @Mock
//    private CrudService<UniDirEntityChildsParent,Long> entityChildsParentCrudService;
//    @Mock
//    private CrudService<UniDirEntityParent,Long> entityParentCrudService;
//    @Mock
//    private CrudService<UniDirEntityParentsChild,Long> entityParentsChildCrudService;
//
//
//    private UniDirEntityParent uniDirEntityParent = new UniDirEntityParent();
//    private UniDirEntityChild uniDirChild = new UniDirEntityChild();
//    private UniDirEntityChildsParent uniDirEntityChildsParent = new UniDirEntityChildsParent();
//    private UniDirEntityParentsChild uniDirEntityParentsChild = new UniDirEntityParentsChild();
//
//    @BeforeEach
//    public void setUp() throws BadEntityException {
//        //instead of mocking
//        MockitoAnnotations.initMocks(this);
//
//        Long entityParentId = 1L;
//        Long entityParentsChildId = 2L;
//        Long childId = 3L;
//        Long childsParentId = 4L;
//
//        uniDirEntityParent.setId(entityParentId);
//        uniDirChild.setId(childId);
//        uniDirEntityChildsParent.setId(childsParentId);
//        uniDirEntityParentsChild.setId(entityParentsChildId);
//
//        when(entityParentCrudService.findById(entityParentId))
//                .thenReturn(Optional.of(uniDirEntityParent));
//        when(entityChildCrudService.findById(childId))
//                .thenReturn(Optional.of(uniDirChild));
//        when(entityParentsChildCrudService.findById(entityParentsChildId))
//                .thenReturn(Optional.of(uniDirEntityParentsChild));
//        when(entityChildsParentCrudService.findById(childsParentId))
//                .thenReturn(Optional.of(uniDirEntityChildsParent));
//
//        when(crudServiceLocator.find(UniDirEntityParent.class))
//                .thenReturn(entityParentCrudService);
//        when(crudServiceLocator.find(UniDirEntityChild.class))
//                .thenReturn(entityChildCrudService);
//        when(crudServiceLocator.find(UniDirEntityParentsChild.class))
//                .thenReturn(entityParentsChildCrudService);
//        when(crudServiceLocator.find(UniDirEntityChildsParent.class))
//                .thenReturn(entityChildsParentCrudService);
//        this.uniDirParentIdResolver = new UniDirParentIdResolver();
//        this.uniDirParentIdResolver.setCrudServiceLocator(getCrudServiceLocator());
//        this.uniDirParentIdResolver.setRelationalDtoManager(new RapidRelationalDtoManager());
//        this.uniDirParentIdResolver.setRelationalEntityManager(new RapidRelationalEntityManager());
//    }
//
//
//    @Test
//    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
//        //given
//        UniDirEntityParent unfinishedMappedUniDirParent = new UniDirEntityParent();
//        UniDirEntityParentDto parentDto = new UniDirEntityParentDto();
//        parentDto.setChildId(getUniDirEntityParentsChild().getId());
//        //when
//        uniDirParentIdResolver.setResolvedEntities(unfinishedMappedUniDirParent,parentDto);
//        //then
//        Assertions.assertEquals(getUniDirEntityParentsChild(),unfinishedMappedUniDirParent.getEntityChild());
//    }
//
//    @Test
//    void resolveDtoIds() {
//        //given
//        UniDirEntityParentDto unfinishedMappedUniDirParentDto = new UniDirEntityParentDto();
//        UniDirEntityParent parent = new UniDirEntityParent();
//        parent.setEntityChild(getUniDirEntityParentsChild());
//        //when
//        uniDirParentIdResolver.setResolvedIds(unfinishedMappedUniDirParentDto,parent);
//        //then
//        Assertions.assertEquals(getUniDirEntityParentsChild().getId(),unfinishedMappedUniDirParentDto.getChildId());
//    }
//}
