package com.github.vincemann.springrapid.entityrelationship.controller.uniDir.abs;


import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityChild;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityChildsParent;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityParent;
import com.github.vincemann.springrapid.entityrelationship.controller.uniDir.testEntities.UniDirEntityParentsChild;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Getter
public abstract class UniDirEntityResolverTest {

    @Mock
    private CrudServiceLocator crudServiceLocator;
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
    public void setUp() throws BadEntityException {
        //instead of mocking
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

        when(crudServiceLocator.find(UniDirEntityParent.class))
                .thenReturn(entityParentCrudService);
        when(crudServiceLocator.find(UniDirEntityChild.class))
                .thenReturn(entityChildCrudService);
        when(crudServiceLocator.find(UniDirEntityParentsChild.class))
                .thenReturn(entityParentsChildCrudService);
        when(crudServiceLocator.find(UniDirEntityChildsParent.class))
                .thenReturn(entityChildsParentCrudService);
    }
}
