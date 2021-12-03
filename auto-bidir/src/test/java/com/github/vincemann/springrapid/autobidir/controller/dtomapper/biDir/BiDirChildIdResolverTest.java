package com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir;


import com.github.vincemann.springrapid.autobidir.RapidRelationalDtoManager;
import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManager;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.abs.BiDirEntityResolverTest;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.testEntities.BiDirEntityChild;
import com.github.vincemann.springrapid.autobidir.controller.dtomapper.biDir.testEntities.BiDirEntityChildDto;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class BiDirChildIdResolverTest extends BiDirEntityResolverTest {
    private BiDirChildIdResolver biDirChildIdResolver;

    @BeforeEach
    @Override
    public void setUp() throws BadEntityException {
        super.setUp();
        this.biDirChildIdResolver = new BiDirChildIdResolver();
        this.biDirChildIdResolver.setCrudServiceLocator(getCrudServiceLocator());
        this.biDirChildIdResolver.setRelationalDtoManager(new RapidRelationalDtoManager());
        this.biDirChildIdResolver.setRelationalEntityManager(new RapidRelationalEntityManager());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityNotFoundException, BadEntityException {
        //given
        BiDirEntityChild unfinishedMappedBiDirEntityChild = new BiDirEntityChild();
        BiDirEntityChildDto biDirEntityChildDto = new BiDirEntityChildDto();
        biDirEntityChildDto.setEntityPId(getBiDirEntityParent().getId());
        biDirEntityChildDto.setSecondEntityPId(getBiDirSecondEntityParent().getId());
        //when
        biDirChildIdResolver.setResolvedEntities(unfinishedMappedBiDirEntityChild, biDirEntityChildDto);
        //then
        Assertions.assertEquals(getBiDirEntityParent(), unfinishedMappedBiDirEntityChild.getBiDirEntityParent());
        Assertions.assertEquals(getBiDirSecondEntityParent(), unfinishedMappedBiDirEntityChild.getBiDirSecondEntityParent());
    }

    @Test
    public void resolveDtoIds(){
        //given
        BiDirEntityChild entityChild = new BiDirEntityChild();
        entityChild.setBiDirEntityParent(getBiDirEntityParent());
        entityChild.setBiDirSecondEntityParent(getBiDirSecondEntityParent());
        BiDirEntityChildDto unfinishedMappedBiDirEntityChildDto = new BiDirEntityChildDto();
        //when
        biDirChildIdResolver.setResolvedIds(unfinishedMappedBiDirEntityChildDto,entityChild);
        //then
        Assertions.assertEquals(getBiDirEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getEntityPId());
        Assertions.assertEquals(getBiDirSecondEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getSecondEntityPId());
    }
}
