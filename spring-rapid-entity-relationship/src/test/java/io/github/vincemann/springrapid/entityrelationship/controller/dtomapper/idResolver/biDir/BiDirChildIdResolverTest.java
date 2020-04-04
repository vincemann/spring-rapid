package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.abs.BiDirEntityResolverTest;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities.BiDirEntityChild;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.idResolver.biDir.testEntities.BiDirEntityChildDto;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
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
    public void setUp() throws NoIdException {
        super.setUp();
        this.biDirChildIdResolver = new BiDirChildIdResolver(getCrudServiceLocator());
    }

    @Test
    public void resolveServiceEntityIds() throws DtoMappingException {
        //given
        BiDirEntityChild unfinishedMappedBiDirEntityChild = new BiDirEntityChild();
        BiDirEntityChildDto biDirEntityChildDto = new BiDirEntityChildDto();
        biDirEntityChildDto.setEntityPId(getBiDirEntityParent().getId());
        biDirEntityChildDto.setSecondEntityPId(getBiDirSecondEntityParent().getId());
        //when
        biDirChildIdResolver.resolveEntityIds(unfinishedMappedBiDirEntityChild, biDirEntityChildDto);
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
        biDirChildIdResolver.resolveDtoIds(unfinishedMappedBiDirEntityChildDto,entityChild);
        //then
        Assertions.assertEquals(getBiDirEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getEntityPId());
        Assertions.assertEquals(getBiDirSecondEntityParent().getId(),unfinishedMappedBiDirEntityChildDto.getSecondEntityPId());
    }
}
