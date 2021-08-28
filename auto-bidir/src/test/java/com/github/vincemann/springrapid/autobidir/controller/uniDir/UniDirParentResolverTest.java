package com.github.vincemann.springrapid.autobidir.controller.uniDir;


import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.UniDirParentIdResolver;
import com.github.vincemann.springrapid.autobidir.controller.uniDir.testEntities.*;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

@Getter
public class UniDirParentResolverTest  {

    private UniDirParentIdResolver uniDirParentIdResolver;

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
        this.uniDirParentIdResolver = new UniDirParentIdResolver(getCrudServiceLocator());
    }


    @Test
    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
        //given
        UniDirEntityParent unfinishedMappedUniDirParent = new UniDirEntityParent();
        UniDirEntityParentDto parentDto = new UniDirEntityParentDto();
        parentDto.setChildId(getUniDirEntityParentsChild().getId());
        //when
        uniDirParentIdResolver.injectEntitiesResolvedFromDtoIdsIntoEntity(unfinishedMappedUniDirParent,parentDto);
        //then
        Assertions.assertEquals(getUniDirEntityParentsChild(),unfinishedMappedUniDirParent.getEntityChild());
    }

    @Test
    void resolveDtoIds() {
        //given
        UniDirEntityParentDto unfinishedMappedUniDirParentDto = new UniDirEntityParentDto();
        UniDirEntityParent parent = new UniDirEntityParent();
        parent.setEntityChild(getUniDirEntityParentsChild());
        //when
        uniDirParentIdResolver.injectEntityIdsResolvedFromEntityIntoDto(unfinishedMappedUniDirParentDto,parent);
        //then
        Assertions.assertEquals(getUniDirEntityParentsChild().getId(),unfinishedMappedUniDirParentDto.getChildId());
    }
}
