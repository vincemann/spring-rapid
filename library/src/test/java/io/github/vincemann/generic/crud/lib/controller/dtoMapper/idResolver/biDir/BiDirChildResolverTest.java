package io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.biDir;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
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
        //given
        BiDirEntityChild unfinishedMappedBiDirEntityChild = new BiDirEntityChild();
        BiDirEntityChildDto biDirEntityChildDto = new BiDirEntityChildDto();
        biDirEntityChildDto.setEntityPId(getBiDirEntityParent().getId());
        biDirEntityChildDto.setSecondEntityPId(getBiDirSecondEntityParent().getId());
        //when
        biDirChildResolver.resolveServiceEntityIds(unfinishedMappedBiDirEntityChild, biDirEntityChildDto);
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
        biDirChildResolver.resolveDtoIds(unfinishedMappedBiDirEntityChildDto,entityChild);
        //then
        Assertions.assertEquals(getBiDirEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getEntityPId());
        Assertions.assertEquals(getBiDirSecondEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getSecondEntityPId());
    }
}
