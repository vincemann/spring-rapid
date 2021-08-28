package com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.abs.BiDirEntityResolverTest;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.testEntities.BiDirEntityParent;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.testEntities.BiDirEntityParentDto;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class BiDirParentIdResolverTest extends BiDirEntityResolverTest {

    private BiDirParentIdResolver biDirParentIdResolver;

    @BeforeEach
    @Override
    public void setUp() throws BadEntityException {
        super.setUp();
        this.biDirParentIdResolver = new BiDirParentIdResolver(getCrudServiceLocator());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
        //given
        BiDirEntityParentDto biDirEntityParentDto = new BiDirEntityParentDto();
        biDirEntityParentDto.setEntityChildId(getBiDirChild().getId());
        BiDirEntityParent unfinishedMappedBiDirEntityParent = new BiDirEntityParent();
        //when
        biDirParentIdResolver.injectEntitiesResolvedFromDtoIdsIntoEntity(unfinishedMappedBiDirEntityParent,biDirEntityParentDto);
        //then
        Assertions.assertEquals(getBiDirChild(),unfinishedMappedBiDirEntityParent.getBiDIrEntityChild());
    }

    @Test
    public void resolveDtoIds(){
        //given
        BiDirEntityParent entityParent = new BiDirEntityParent();
        entityParent.setBiDIrEntityChild(getBiDirChild());
        BiDirEntityParentDto unfinishedMappedBiDirEntityParentDto = new BiDirEntityParentDto();
        //when
        biDirParentIdResolver.injectEntityIdsResolvedFromEntityIntoDto(unfinishedMappedBiDirEntityParentDto,entityParent);
        //then
        Assertions.assertEquals(getBiDirChild().getId(), unfinishedMappedBiDirEntityParentDto.getEntityChildId());
    }
}
