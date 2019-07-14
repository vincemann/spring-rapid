package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.abs;


import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityChild;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityChildsParent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityParent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.uniDir.testEntities.UniDirEntityParentsChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Getter
public abstract class UniDirEntityResolverTest {

    @Mock
    private CrudServiceFinder crudServiceFinder;
    @Mock
    private CrudService<UniDirEntityChild,Long> entityChildCrudService;
    @Mock
    private CrudService<UniDirEntityChildsParent,Long> entityChildsParentCrudService;
    @Mock
    private CrudService<UniDirEntityParent,Long> entityParentCrudService;
    @Mock
    private CrudService<UniDirEntityParentsChild,Long> entityParentsChildCrudService;


    private UniDirEntityParent uniDirEntityParent = new UniDirEntityParent();
    private UniDirEntityChild uniDirChild = new UniDirEntityChild();
    private UniDirEntityChildsParent uniDirEntityChildsParent = new UniDirEntityChildsParent();
    private UniDirEntityParentsChild uniDirEntityParentsChild = new UniDirEntityParentsChild();

    @BeforeEach
    public void setUp() throws NoIdException {
        MockitoAnnotations.initMocks(this);

        Long entityParentId = 1L;
        Long entityParentsChildId = 2L;
        Long childId = 3L;
        Long childsParentId = 4L;

        uniDirEntityParent.setId(entityParentId);
        uniDirChild.setId(childId);
        uniDirEntityChildsParent.setId(childsParentId);
        uniDirEntityParentsChild.setId(entityParentsChildId);

        when(entityParentCrudService.findById(entityParentId))
                .thenReturn(Optional.of(uniDirEntityParent));
        when(entityChildCrudService.findById(childId))
                .thenReturn(Optional.of(uniDirChild));
        when(entityParentsChildCrudService.findById(entityParentsChildId))
                .thenReturn(Optional.of(uniDirEntityParentsChild));
        when(entityChildsParentCrudService.findById(childsParentId))
                .thenReturn(Optional.of(uniDirEntityChildsParent));

        Map<Class<? extends IdentifiableEntity>,CrudService> classCrudServiceMap = new HashMap<>();
        classCrudServiceMap.put(UniDirEntityParent.class,entityParentCrudService);
        classCrudServiceMap.put(UniDirEntityChild.class,entityChildCrudService);
        classCrudServiceMap.put(UniDirEntityParentsChild.class,entityParentsChildCrudService);
        classCrudServiceMap.put(UniDirEntityChildsParent.class,entityChildsParentCrudService);

        when(crudServiceFinder.getCrudServices())
                .thenReturn(classCrudServiceMap);
    }
}
