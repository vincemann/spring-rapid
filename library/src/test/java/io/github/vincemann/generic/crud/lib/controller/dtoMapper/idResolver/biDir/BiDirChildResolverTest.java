package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.*;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.abs.BiDirEntityResolverTest;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities.BiDirEntityChild;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir.testEntities.BiDirEntityChildDto;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
public class BiDirChildResolverTest extends BiDirEntityResolverTest {
    private BiDirChildResolver biDirChildResolver;

    @BeforeEach
    @Override
    public void setUp() throws NoIdException {
        super.setUp();
        this.biDirChildResolver = new BiDirChildResolver(getCrudServiceFinder());
    }

    @Test
    public void resolveServiceEntityIds() throws EntityMappingException {
        //when
        BiDirEntityChild unfinishedMappedBiDirEntityChild = new BiDirEntityChild();
        BiDirEntityChildDto biDirEntityChildDto = new BiDirEntityChildDto();
        biDirEntityChildDto.setEntityPId(getBiDirEntityParent().getId());
        biDirEntityChildDto.setSecondEntityPId(getBiDirSecondEntityParent().getId());
        //do
        biDirChildResolver.resolveServiceEntityIds(unfinishedMappedBiDirEntityChild, biDirEntityChildDto);
        //then
        Assertions.assertEquals(getBiDirEntityParent(), unfinishedMappedBiDirEntityChild.getBiDirEntityParent());
        Assertions.assertEquals(getBiDirSecondEntityParent(), unfinishedMappedBiDirEntityChild.getBiDirSecondEntityParent());
    }

    @Test
    public void resolveDtoIds(){
        //when
        BiDirEntityChild entityChild = new BiDirEntityChild();
        entityChild.setBiDirEntityParent(getBiDirEntityParent());
        entityChild.setBiDirSecondEntityParent(getBiDirSecondEntityParent());
        BiDirEntityChildDto unfinishedMappedBiDirEntityChildDTO = new BiDirEntityChildDto();
        //do
        biDirChildResolver.resolveDtoIds(unfinishedMappedBiDirEntityChildDTO,entityChild);
        //then
        Assertions.assertEquals(getBiDirEntityParent().getId(),unfinishedMappedBiDirEntityChildDTO.getEntityPId());
        Assertions.assertEquals(getBiDirSecondEntityParent().getId(),unfinishedMappedBiDirEntityChildDTO.getSecondEntityPId());
    }
}
